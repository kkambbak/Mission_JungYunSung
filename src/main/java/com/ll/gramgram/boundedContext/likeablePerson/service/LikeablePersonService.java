package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {
        if ( member.hasConnectedInstaMember() == false ) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }
        InstaMember fromInstaMember = member.getInstaMember();
        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(fromInstaMember) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(member.getInstaMember().getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();


        Optional<LikeablePerson> opFoundLikeablePerson = findLikeablePersonMatchingToInstaMemberId(fromInstaMember.getFromLikeablePeople(), toInstaMember.getId());
        if (opFoundLikeablePerson.isPresent()) {
            int foundTypeCode = opFoundLikeablePerson.get().getAttractiveTypeCode();

            if(foundTypeCode == likeablePerson.getAttractiveTypeCode()){
                return RsData.of("F-3", "이미 등록한 상대입니다.");
            }
            else{
                //호감표시 수정 코드
                return modifyAttractiveTypeCode(opFoundLikeablePerson.get(), likeablePerson.getAttractiveTypeCode());
            }
        }
        if(fromInstaMember.getFromLikeablePeople().size() >= 10){
            return RsData.of("F-4", "호감상대는 최대 10명까지만 등록할 수 있습니다.");
        }

        likeablePersonRepository.save(likeablePerson); // 저장

        // 너가 좋아하는 호감표시 생겼어.
        fromInstaMember.addFromLikeablePerson(likeablePerson);

        // 너를 좋아하는 호감표시 생겼어.
        toInstaMember.addToLikeablePerson(likeablePerson);

        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    public Optional<LikeablePerson> findLikeablePersonMatchingToInstaMemberId(List<LikeablePerson> likeablePersonList, long id){
        return likeablePersonList.stream().filter(likeablePerson1 ->
                        likeablePerson1.getToInstaMember().getId().equals(id))
                .findAny();
    }

    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }

    @Transactional
    public RsData<LikeablePerson> delete(Member member, Long likeablePersonId){
        Optional<LikeablePerson> deleteData = likeablePersonRepository.findById(likeablePersonId);

        if(deleteData.isEmpty() || member.getInstaMember()==null){
            return RsData.of("F-2", "삭제하려는 호감정보가 없습니다.");
        }
        else if(!deleteData.get().getFromInstaMember().getId().equals(member.getInstaMember().getId())){
            return RsData.of("F-3", "본인의 호감정보만 삭제할 수 있습니다.");
        }

        likeablePersonRepository.delete(deleteData.get());
        return RsData.of("S-1", "%s번 호감정보가 삭제되었습니다.".formatted(likeablePersonId));
    }

    @Transactional
    public RsData<LikeablePerson> modifyAttractiveTypeCode(LikeablePerson likeablePerson, int TypeCode){
        //호감 수정
        likeablePerson.changeAttractiveTypeCode(TypeCode);

        likeablePersonRepository.save(likeablePerson);
        return RsData.of("S-2", "%s의 호감사유를 변경합니다.".formatted(likeablePerson.getToInstaMemberUsername()));
    }
}
