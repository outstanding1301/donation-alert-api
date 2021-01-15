# 💸 DonationAlertAPI
[Twip](http://twip.kr/), [Toonation](https://toon.at/)의 후원 알림(Alertbox)을 받아올 수 있는 RxJava 기반 Java API  

![so much money](https://media.giphy.com/media/3orif1esInVTdhaNsk/giphy.gif)

> [outstandingboy/DonationAlertAPI](https://github.com/outstanding1301/DonationAlertAPI)  

----

# Start

## Twip
```java
// Twip Alertbox URL의 마지막 https://twip.kr/widgets/alertbox/<YOUR_TWIP_KEY> 부분을 입력하세요.
Twip twip = new Twip("YOUR_TWIP_KEY");

// 메시지를 구독합니다.
// 연결 알림, 에러 등의 String 메시지를 처리하는 핸들러를 인자로 사용합니다. 
twip.subscribeMessage(s -> System.out.println(s));

// 도네이션 알림을 구독합니다.
// Donation 객체를 처리하는 핸들러를 인자로 사용합니다.
twip.subscribeDonation(donation -> {
    System.out.println("[Twip] "+donation.getNickName()+"님이 "+donation.getAmount()+"원을 후원했습니다.");
    System.out.println("후원 내용: "+donation.getComment());
});
```

## Toonation
```java
// Toonation Alertbox URL의 마지막 https://toon.at/widget/alertbox/<YOUR_TOONATION_KEY> 부분을 입력하세요.
Toonation toonation = new Toonation("YOUR_TOONATION_KEY");

// 메시지를 구독합니다.
// 연결 알림, 에러 등의 String 메시지를 처리하는 핸들러를 인자로 사용합니다. 
toonation.subscribeMessage(s -> System.out.println(s));

// 도네이션 알림을 구독합니다.
// Donation 객체를 처리하는 핸들러를 인자로 사용합니다.
toonation.subscribeDonation(donation -> {
    System.out.println("[Toonation] "+donation.getNickName()+"님이 "+donation.getAmount()+"원을 후원했습니다.");
    System.out.println("후원 내용: "+donation.getComment());
});
```

# Docs

## Donation

| 식별자 | 타입 | 설명 |
|:---:|:---:|:---:|
| id | String | 후원자 ID |
| nickname | String | 후원자 닉네임 |
| comment | String | 후원 내용 |
| amount | Integer | 후원 금액 |

<br>

## Platform (Twip, Toonation)

| 식별자 | 타입 | 설명 |
|:---:|:---:|:---:|
| subscribeDonation(Consumer<Donation> onNext) | void | 후원 알림 구독 |
| subscribeMessage(Consumer<String> onNext) | void | API 메시지 구독 |
| close() | void | 연결 종료 |
| getDonationObservable() | Subject<Donation> | 후원 알림 Subject 객체 반환 |
| getMessageObservable() | Subject<String> | API 메시지 Subject 객체 반환 |

# Dependencies
```gradle
testCompile group: 'junit', name: 'junit', version: '4.12'
implementation 'io.socket:socket.io-client:1.0.0'
implementation  'io.reactivex.rxjava2:rxjava:2.1.16'
implementation 'org.jsoup:jsoup:1.13.1'
implementation 'com.googlecode.json-simple:json-simple:1.1.1'
```