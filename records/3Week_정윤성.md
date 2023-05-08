# 3Week_정윤성.md

## Title: [3Week] 정윤성

### 미션 요구사항 분석 & 체크리스트

---
- 네이버클라우드플랫폼을 통한 배포(도메인 없이, IP로 접속)

- 호감표시/호감사유변경 후, 개별 호감표시건에 대해서, 3시간 동안은 호감취소와 호감사유변경을 할 수 없도록 작업


### 3주차 미션 요약

---

**[접근 방법]**

1. coolTime 가져오기  
`private final long coolTime = AppConfig.getLikeablePersonModifyCoolTime();`
  
  로 coolTime을 가져옴.

  
  
2. canCancel과 canModify에서 쿨타임적용하기.

  ```java
//LikeablePersonService.java
long cool = (long) Math.ceil(coolTime/3600.0);
if (Duration.between(likeablePerson.getModifyDate(), LocalDateTime.now()).toHours() < cool )
    return RsData.of("F-8", "호감 표시 / 마지막 수정 후 %d시간이 지나야 삭제 가능합니다.".formatted(cool));
```

호감 변경 / 삭제시 3시간뒤에 가능하다는 메시지를 띄워줌.
  
3. 호감표시한 후에 쿨타임 알려주기.

```java
//LikeablePerson.java
public String getModifyUnlockDateRemainStrHuman() {
    return "%d시 %d분".formatted(modifyUnlockDate.getHour(), modifyUnlockDate.getMinute());
}
```


4. 배포

https://www.kkbk.me 에서 확인 가능

## 아쉬웠던 점/궁금했던 점
  
선택미션 구현중.