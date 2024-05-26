# 💸 Donation Alert API
[Twip](http://twip.kr/), [Toonation](https://toon.at/)의 후원 알림(Alertbox)을 받아올 수 있는 RxJava 기반 Java API  

![so much money](https://media.giphy.com/media/3orif1esInVTdhaNsk/giphy.gif)

> [outstandingboy/donation-alert-api](https://github.com/outstanding1301/donation-alert-api)  

----

# 🚀 Start
## Gradle (use [Jitpack](https://jitpack.io/))
```gradle
repositories {
    ...
    maven { url 'https://jitpack.io' }
}

dependencies {
    ...
    compile 'com.github.outstanding1301:donation-alert-api:2.0.0'
}
```

```maven
<repositories>
    ...
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
    ...
</repositories>

<dependency>
    ...
    <groupId>com.github.outstanding1301</groupId>
    <artifactId>donation-alert-api</artifactId>
    <version>2.0.0</version>
    ...
</dependency>
```

## Twip
```java
// Twip Alertbox URL의 마지막 https://twip.kr/widgets/alertbox/<YOUR_TWIP_KEY> 부분을 입력하세요.
Twip twip = new Twip("YOUR_TWIP_KEY");

// 도네이션 알림을 구독합니다.
String donationSubscriptionId = twip.getDonationTopic().subscribe(donation -> {
    System.out.println("[Twip] " + donation.getNickName() + "님이 " + donation.getAmount() + "원을 후원했습니다.");
    System.out.println("후원 내용: " + donation.getComment());
});

// 메세지를 구독합니다.
twip.getMessageTopic().subscribe(message -> {
    Message.Key key = message.getKey();
    if (key == Message.Key.TWIP_CONNECT) {
        Object[] args = (Object[]) message.getValue();
    }
    if (key == Message.Key.TWIP_CONNECT_ERROR) {
        Object[] args = (Object[]) message.getValue();
    }
    if (key == Message.Key.TWIP_ERROR) {
        Object[] args = (Object[]) message.getValue();
    }
    if (key == Message.Key.TWIP_DISCONNECT) {
        Object[] args = (Object[]) message.getValue();
    }
    ...
});

// (1.x.x 버전과 동일) 메세지를 구독합니다.
twip.getLegacyMessageTopic().subscribe(System.out::println);

// 구독을 취소합니다.
twip.getDonationTopic().unsubscribe(donationSubscriptionId);
```

## Toonation
```java
// Toonation Alertbox URL의 마지막 https://toon.at/widget/alertbox/<YOUR_TOONATION_KEY> 부분을 입력하세요.
Toonation toonation = new Toonation("YOUR_TOONATION_KEY");

// 도네이션 알림을 구독합니다.
String donationSubscriptionId = toonation.getDonationTopic().subscribe(donation -> {
    System.out.println("[Toonation] " + donation.getNickName() + "님이 " + donation.getAmount() + "원을 후원했습니다.");
    System.out.println("후원 내용: " + donation.getComment());
});

// 메세지를 구독합니다.
toonation.getMessageTopic().subscribe(message -> {
    Message.Key key = message.getKey();
    if (key == Message.Key.TOONATION_OPEN) {
        
    }
    if (key == Message.Key.TOONATION_MESSAGE) {
        String text = (String) message.getValue();
    }
    if (key == Message.Key.TOONATION_CLOSED) {
        int code = (int) message.getValue();
    }
    if (key == Message.Key.TOONATION_FAILURE) {
        Throwable t = (Throwable) message.getValue();
    }
    ...
});

// (1.x.x 버전과 동일) 메세지를 구독합니다.
toonation.getLegacyMessageTopic().subscribe(System.out::println);

// 구독을 취소합니다.
toonation.getDonationTopic().unsubscribe(donationSubscriptionId);
```

## Chzzk
```java
// 치지직 방송 채널 URL의 마지막 https://chzzk.naver.com/live/<YOUR_CHZZK_KEY> 부분을 입력하세요.
Chzzk chzzk = new Chzzk("YOUR_CHZZK_KEY");

// 도네이션 알림을 구독합니다.
String donationSubscriptionId = chzzk.getDonationTopic().subscribe(donation -> {
    System.out.println("[Chzzk] " + donation.getNickName() + "님이 " + donation.getAmount() + "원을 후원했습니다.");
    System.out.println("후원 내용: " + donation.getComment());
});

// 메세지를 구독합니다.
chzzk.getMessageTopic().subscribe(message -> {
    Message.Key key = message.getKey();
    if (key == Message.Key.CHZZK_OPEN) {
        
    }
    if (key == Message.Key.CHZZK_MESSAGE) {
        String text = (String) message.getValue();
    }
    if (key == Message.Key.CHZZK_CLOSED) {
        int code = (int) message.getValue();
    }
    if (key == Message.Key.CHZZK_FAILURE) {
        Throwable t = (Throwable) message.getValue();
    }
    ...
});

// (1.x.x 버전과 동일) 메세지를 구독합니다.
chzzk.getLegacyMessageTopic().subscribe(System.out::println);

// 구독을 취소합니다.
chzzk.getDonationTopic().unsubscribe(donationSubscriptionId);
```

# 💉 Dependencies
```gradle
implementation 'io.socket:socket.io-client:1.0.2'
implementation 'com.google.code.gson:gson:2.10.1'
```
