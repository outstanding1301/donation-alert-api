package com.outstandingboy.donationalert.entity;

import com.google.gson.Gson;
import com.outstandingboy.donationalert.platform.Chzzk;
import com.outstandingboy.donationalert.platform.Toonation;
import com.outstandingboy.donationalert.platform.Twip;
import com.outstandingboy.donationalert.util.Gsons;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class DonationTest {
    Gson gson = new Gson();

    @Test
    void twipSerialize() {
        String payload = "{\"watcher_id\":\"big_seal_0\",\"amount\":5000,\"ttsurl\":[],\"variation_id\":null,\"repeat\":true,\"effect\":{},\"nickname\":\"big_seal_0\",\"comment\":\"5000원을 받은 줄 알았는데, 알고보니 후원 테스트 중이었네요. Kappa Kappa\",\"_id\":\"TEST\",\"slotmachine_data\":null,\"subbed\":false,\"ttstype\":\"heyguys\"}";
        TwipDonationPayload donation = gson.fromJson(payload, TwipDonationPayload.class);
        Assertions.assertEquals(donation.getWatcherId(), "big_seal_0");
        Assertions.assertEquals(donation.getAmount(), 5000L);
        Assertions.assertEquals(donation.getNickname(), "big_seal_0");
        Assertions.assertEquals(donation.getComment(), "5000원을 받은 줄 알았는데, 알고보니 후원 테스트 중이었네요. Kappa Kappa");
    }

    @Test
    void toonationSerialize() {
        String payload = "{\"test\":1,\"code\":101,\"content\":{\"message\":\"후원 테스트 입니다.\",\"amount\":1000,\"uid\":\"ea82b62e5696f35bdfc7046b6301b95d\",\"account\":\"big_seal_\",\"name\":\"빅물개\",\"image\":\"https://static-cdn.jtvnw.net/user-default-pictures-uv/cdd517fe-def4-11e9-948e-784f43822e80-profile_image-300x300.png\",\"acctype\":1,\"test_noti\":1,\"level\":1,\"tts_locale\":\"junwoo\",\"tts_provider\":\"voiceware\",\"conf_idx\":1,\"rec_link\":\"\",\"video_info\":null,\"video_begin\":0,\"video_length\":0,\"tts_link\":\"https://cdn2.toon.at/asset/__special_tts_cache__/Ea8cqSOV96LP7G4CmsmxoPNaUpq0S6SWsDjnexI8gSLfYdOBaBwFS-iV_yXA_EAUJmmgVtEjb77URLGfFWv2ag.mp4\"}}";
        ToonationDonationPayload donation = gson.fromJson(payload, ToonationDonationPayload.class);
        Assertions.assertEquals(donation.getContent().getAccount(), "big_seal_");
        Assertions.assertEquals(donation.getContent().getAmount(), 1000L);
        Assertions.assertEquals(donation.getContent().getName(), "빅물개");
        Assertions.assertEquals(donation.getContent().getMessage(), "후원 테스트 입니다.");
    }

    @Test
    void chzzkSerialize() {
        String payload = "{\"svcid\":\"game\",\"ver\":\"1\",\"bdy\":[{\"svcid\":\"game\",\"cid\":\"N1AQgn\",\"mbrCnt\":1439,\"uid\":\"2b0eee4611d302a478d152a4c7bdd3cb\",\"profile\":\"{\\\"userIdHash\\\":\\\"2b0eee4611d302a478d152a4c7bdd3cb\\\",\\\"nickname\\\":\\\"아무예요\\\",\\\"profileImageUrl\\\":\\\"\\\",\\\"userRoleCode\\\":\\\"common_user\\\",\\\"badge\\\":null,\\\"title\\\":null,\\\"verifiedMark\\\":false,\\\"activityBadges\\\":[{\\\"badgeNo\\\":846465,\\\"badgeId\\\":\\\"donation_accumulate_amount_lv1\\\",\\\"imageUrl\\\":\\\"https://ssl.pstatic.net/static/nng/glive/icon/cheese01.png\\\",\\\"activated\\\":true}],\\\"streamingProperty\\\":{}}\",\"msg\":\"나도 그런가하고 방금 해봤는데 명훈님보다 한참 안좋은컴인데도 프레임드랍 없음 설정 다시 만져야할듯?\",\"msgTypeCode\":10,\"msgStatusType\":\"NORMAL\",\"extras\":\"{\\\"emojis\\\":{},\\\"isAnonymous\\\":false,\\\"payType\\\":\\\"CURRENCY\\\",\\\"payAmount\\\":1000,\\\"streamingChannelId\\\":\\\"5d53f8fa5bef9b1bd4dc884f9907c079\\\",\\\"nickname\\\":\\\"아무예요\\\",\\\"osType\\\":\\\"PC\\\",\\\"donationType\\\":\\\"CHAT\\\",\\\"weeklyRankList\\\":[{\\\"userIdHash\\\":\\\"bc0bb4421437a81507779821c36a2c1e\\\",\\\"nickName\\\":\\\"혜윰o\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":500000,\\\"ranking\\\":1},{\\\"userIdHash\\\":\\\"0ae3723f45fb4afe007029335095e29e\\\",\\\"nickName\\\":\\\"이솔보리\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":31000,\\\"ranking\\\":2},{\\\"userIdHash\\\":\\\"0bc390a8bbcde435f771bf22e04b0621\\\",\\\"nickName\\\":\\\"덴경대\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":23000,\\\"ranking\\\":3},{\\\"userIdHash\\\":\\\"e4d99ed5689a3ed68d57458854ae8f86\\\",\\\"nickName\\\":\\\"빡포션\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":18000,\\\"ranking\\\":4},{\\\"userIdHash\\\":\\\"3e3f275d6df594dbdc4f907dee40e381\\\",\\\"nickName\\\":\\\"미분\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":13000,\\\"ranking\\\":5},{\\\"userIdHash\\\":\\\"ca694b049b68b670432b657a5877680d\\\",\\\"nickName\\\":\\\"불명예훈장\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":11000,\\\"ranking\\\":6},{\\\"userIdHash\\\":\\\"2b0eee4611d302a478d152a4c7bdd3cb\\\",\\\"nickName\\\":\\\"아무예요\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":10000,\\\"ranking\\\":7},{\\\"userIdHash\\\":\\\"2e70454ccacb2f778c72189ef5f82c83\\\",\\\"nickName\\\":\\\"동동꾸리\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":6000,\\\"ranking\\\":8},{\\\"userIdHash\\\":\\\"f21497032ad21c4fb54cb2fed8bceec3\\\",\\\"nickName\\\":\\\"라면먹는그브\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":5000,\\\"ranking\\\":9},{\\\"userIdHash\\\":\\\"b9b4e5f489b0a3706ede4976abe0eaf9\\\",\\\"nickName\\\":\\\"시 도\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":5000,\\\"ranking\\\":10}],\\\"donationUserWeeklyRank\\\":{\\\"userIdHash\\\":\\\"2b0eee4611d302a478d152a4c7bdd3cb\\\",\\\"nickName\\\":\\\"아무예요\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":10000,\\\"ranking\\\":7},\\\"chatType\\\":\\\"STREAMING\\\"}\",\"ctime\":1714752654401,\"utime\":1714752654401,\"msgTid\":null,\"msgTime\":1714752654401}],\"cmd\":93102,\"tid\":null,\"cid\":\"N1AQgn\"}";
        ChzzkDonationPayload donation = Gsons.gson().fromJson(payload, ChzzkDonationPayload.class);
        Assertions.assertEquals(donation.getBody().getProfile().getUserIdHash(), "2b0eee4611d302a478d152a4c7bdd3cb");
        Assertions.assertEquals(donation.getBody().getExtras().getPayAmount(), 1000L);
        Assertions.assertEquals(donation.getBody().getProfile().getNickname(), "아무예요");
        Assertions.assertEquals(donation.getBody().getMsg(), "나도 그런가하고 방금 해봤는데 명훈님보다 한참 안좋은컴인데도 프레임드랍 없음 설정 다시 만져야할듯?");
    }

    public static void main(String[] args) {
        Chzzk t = new Chzzk("1b0561f3051c10a24b9d8ec9a6cb3374");
        t.subscribeMessage(System.out::println);
        t.subscribeDonation(System.out::println);

        try {
            TimeUnit.MINUTES.sleep(60);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}