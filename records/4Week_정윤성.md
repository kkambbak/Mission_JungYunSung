# 4Week_정윤성.md

## Title: [4Week] 정윤성

### 미션 요구사항 분석 & 체크리스트

---

- 필수 미션
- [x] 네이버클라우드플랫폼을 통한 배포, 도메인, HTTPS 까지 적용
- [x] 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 성별 필터링기능 구현


- 선택미션
- [x] 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 호감사유 필터링기능 구현
- [x] 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 정렬기능
- [x] 젠킨스로 main브랜치 커밋시 자동 배포


### 4주차 미션 요약

---


**[접근 방법]**

1. 성별 필터링 기능

```java
@PreAuthorize("isAuthenticated()")  
@GetMapping("/toList")  
public String showToList(Model model,  
                         @RequestParam(required = false) String gender,  
                         @RequestParam(required = false) Integer attractiveTypeCode,  
                         @RequestParam(required = false) Integer sortCode) {  
    InstaMember instaMember = rq.getMember().getInstaMember();  
  
    // 인스타인증을 했는지 체크  
    if (instaMember != null) {  
        // 해당 인스타회원이 좋아하는 사람들 목록  
        List<LikeablePerson> likeablePeople = instaMember.getToLikeablePeople();  
  
        if (!Objects.equals(gender, "") && gender != null){  
            likeablePeople = likeablePeople.stream()  
                    .filter(p -> p.getFromInstaMember().getGender().equals(gender))  
                    .collect(Collectors.toList());  
        }
    ...
    }
}
```

RequestParam으로 파라미터를 받고,

파라미터 gender가 ""이나 null이 아니면 stream으로 처리했다.

null만 처리하면, 쿼리스트링없이 /toList 만 요청받았을때 아무것도 안 나오는 것을 ""도 체크하도록 해결.

2. 호감사유 필터링 기능

```java
if (attractiveTypeCode != null){  
    likeablePeople = likeablePeople.stream()  
            .filter(p -> p.getAttractiveTypeCode() == attractiveTypeCode)  
            .collect(Collectors.toList());  
}
```

성별 필터링과 비슷하게 스트림으로 처리.


3. 정렬기능

```java
if (sortCode != null){  
    switch (sortCode){  
        case 1:  
            likeablePeople.sort(Comparator.comparing(BaseEntity::getModifyDate));  
            break;        
        case 2:  
            likeablePeople.sort(Comparator.comparing(BaseEntity::getModifyDate).reversed());  
            break;        
        case 3:  
            //인기 많은순  
            likeablePeople.sort(Comparator.comparing((LikeablePerson p) -> p.getFromInstaMember().getLikes()).reversed());  
            break;        
        case 4:  
            //인기 적은순  
            likeablePeople.sort(Comparator.comparing(p->p.getFromInstaMember().getLikes()));  
            break;        
        case 5:  
            likeablePeople.sort(Comparator.comparing(p->p.getFromInstaMember().getGender()));  
            break;        
        case 6:  
            likeablePeople.sort(Comparator.comparing(LikeablePerson::getAttractiveTypeCode));  
            break;    
    }  
}
```

좀 단순하게 switch문으로 sort를 돌렸다.

인기많은순은 내림차순임을 유의.


4. 배포

https는 저번주에 이미 적용했었고,

젠킨스에서 파이프라인 작성 후 깃허브 웹훅으로 main브랜치에 커밋시 자동 배포

작업 내용은 레인보우위키 230509에서 확인가능하다.

https://kkbk.me

## 아쉬웠던 점/궁금했던 점

정렬기능을 좀 더 깔끔하게 구현할 수 있을까