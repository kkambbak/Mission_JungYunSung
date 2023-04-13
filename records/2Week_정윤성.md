# 2Week_정윤성.md

## Title: [2Week] 정윤성

### 미션 요구사항 분석 & 체크리스트

---

- [x]  케이스4: 중복 호감 표시 불가
    - [x]  테케 작성
    - [x]  중복 호감표시 실패처리
- [x]  케이스5: 11명이상의 호감상대 등록 x
    - [x]  테스트케이스 작성
    - [x]  10명 이상의 호감표시 실패처리
- [x]  케이스6: 케이스4가 발생했을 때, 다른 사유로 호감을 표시하는 경우 성공으로 처리
    - [x]  테스트케이스 작성
    - [x]  다른사유로 호감표시시 성공처리
    - [x]  케이스4와 엮어서 리팩토링
- [x]  Max값을 application.yml에서 관리

### 2주차 미션 요약

---

1. 케이스 4: 중복 호감 표시 불가

    ```java
    //LikeablePersonService.like()
    if(fromInstaMember.getFromLikeablePeople().stream().anyMatch(i->i.getToInstaMember().equals(toInstaMember))){
        return RsData.of("F-3", "이미 등록한 상대입니다.");
    }
    ```

   like메소드 내에서 다음을 추가하였다.

   getFromlikeablePeople()로 가져온 리스트에서 toInstaMember가 같은 것이 있는지 anyMatch로 체크한 후 True 일경우 F-3으로 처리.

2. 케이스 5: 11명이상의 호감상대 등록x

    ```java
    //LikeablePersonService.like()
    if(fromInstaMember.getFromLikeablePeople().size() >= 10){
        return RsData.of("F-4", "호감상대는 최대 10명까지만 등록할 수 있습니다.");
    }
    ```

   size가 10이상일 경우 F-4로 처리

3. 케이스 6: 케이스4가 발생했을 때, 다른 사유로 호감을 표시하는 경우 성공으로 처리하기

    ```java
    //LikeablePersonService
    @Transactional
    public RsData<LikeablePerson> modifyAttractiveTypeCode(LikeablePerson likeablePerson, int TypeCode){
        //호감 수정
        likeablePerson.changeAttractiveTypeCode(TypeCode);
    
        likeablePersonRepository.save(likeablePerson);
        return RsData.of("S-2", "%s의 호감사유를 변경합니다.".formatted(likeablePerson.getToInstaMemberUsername()));
    }
    ```

    ```java
    //LikeablePerson.java
    public void changeAttractiveTypeCode(int attractiveTypeCode) {
        this.attractiveTypeCode = attractiveTypeCode;
    }
    ```

4. 리팩토링

    ```java
    
    public Optional<LikeablePerson> findLikeablePersonMatchingToInstaMemberId(List<LikeablePerson> likeablePersonList, long id){
        return likeablePersonList.stream().filter(likeablePerson1 ->
                        likeablePerson1.getToInstaMember().getId().equals(id))
                .findAny();
    }
    ```

   먼저 if문의 조건이었던 것을, 따로 메소드로 분리하였다.

   anyMatch였던 것을 filter와 findAny로 opLikeablePerson을 반환하도록 변경하였다.

    ```java
    //LikeablePersonService.like()
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
    ```

   다음과 같이 변경하였다.

5. Max값을 application.yml에서 관리하도록 변경

    ```yaml
    custom:
      likeablePerson:
        max: 10
    ```

   application.yml에 다음을 추가

    ```java
    //base.initData.AppConfig
    @Configuration
    public class AppConfig {
        @Getter
        private static long likeablePersonMax;
    
        @Value("${custom.likeablePerson.max}")
        public void setLikeablePersonMax(long likeablePersonMax) {
            AppConfig.likeablePersonMax = likeablePersonMax;
        }
    }
    ```

   AppConfig에서 getter와 setter를 만들어준다.

    ```java
    //LikeablePersonService
    private final long likeablePeopleMax = AppConfig.getLikeablePersonMax();
    
    if(fromInstaMember.getFromLikeablePeople().size() >= likeablePeopleMax){
        return RsData.of("F-4", "호감상대는 최대 %n명까지만 등록할 수 있습니다.".formatted(likeablePeopleMax));
    }
    ```

   단순 숫자10이었던 것을 다음처럼 변경

**[특이사항]**

구현 과정에서 아쉬웠던 점 / 궁금했던 점을 정리합니다.

- like 메소드가 좀 많이 복잡해진 것 같아서 빼낼 수 있는 부분은 따로 또 빼내야 되지 싶다.