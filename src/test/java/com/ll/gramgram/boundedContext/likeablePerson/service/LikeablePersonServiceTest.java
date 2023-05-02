package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.member.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class LikeablePersonServiceTest {
    @Autowired
    private LikeablePersonService likeablePersonService;

    @Test
    @DisplayName("기존의 사유와 다른 사유로 호감을 표시하는 경우에는 사유만 수정하고 성공으로 처리")
    void t001() throws Exception {
        //given
        InstaMember instaMember = likeablePersonService.findById(1L).get().getFromInstaMember();
        Member member = Member.builder().username("user3").password("1234").instaMember(instaMember).build();
        likeablePersonService.like(member, "insta_user4", 1);

        //When
        RsData<LikeablePerson> likeRsData = likeablePersonService.like(member, "insta_user4", 2);

        //Then
        Assertions.assertThat(likeablePersonService.findById(1L).get().getAttractiveTypeCode()).isEqualTo(2);
        Assertions.assertThat(likeRsData.getResultCode()).isEqualTo("S-3");
    }

    @Test
    @DisplayName("설정 파일에 있는 최대 호감 표시 수 가져오기")
    void t002() throws Exception {
        long likeablePersonMax = AppConfig.getLikeablePersonFromMax();

        Assertions.assertThat(likeablePersonMax).isEqualTo(10);
    }
}