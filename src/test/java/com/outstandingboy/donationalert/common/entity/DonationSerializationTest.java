package com.outstandingboy.donationalert.common.entity;

import com.google.gson.Gson;
import com.outstandingboy.donationalert.platform.chzzk.entity.ChzzkPayload;
import com.outstandingboy.donationalert.platform.toonation.entity.ToonationPayload;
import com.outstandingboy.donationalert.platform.twip.entity.TwipPayload;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class DonationSerializationTest {
    Gson gson = new Gson();

    @Test
    void twipSerialize() {
        String payload = "{\"watcher_id\":\"jjangjjangman0\",\"amount\":5000,\"ttsurl\":[],\"variation_id\":null,\"repeat\":true,\"effect\":{},\"nickname\":\"짱짱맨\",\"comment\":\"내 돈을 받아라\",\"_id\":\"TEST\",\"slotmachine_data\":null,\"subbed\":false,\"ttstype\":\"heyguys\"}";
        TwipPayload donation = gson.fromJson(payload, TwipPayload.class);
        Assertions.assertEquals(donation.getWatcherId(), "jjangjjangman0");
        Assertions.assertEquals(donation.getAmount(), 5000L);
        Assertions.assertEquals(donation.getNickname(), "짱짱맨");
        Assertions.assertEquals(donation.getComment(), "내 돈을 받아라");
    }

    @Test
    void toonationSerialize() {
        String payload = "{\"test\":1,\"code\":101,\"content\":{\"message\":\"내 돈을 받아라\",\"amount\":1000,\"uid\":\"ea82b62e5696f35bdfc7046b6301b95d\",\"account\":\"jjangjjangman\",\"name\":\"짱짱맨\",\"image\":\"https://static-cdn.jtvnw.net/user-default-pictures-uv/cdd517fe-def4-11e9-948e-784f43822e80-profile_image-300x300.png\",\"acctype\":1,\"test_noti\":1,\"level\":1,\"tts_locale\":\"junwoo\",\"tts_provider\":\"voiceware\",\"conf_idx\":1,\"rec_link\":\"\",\"video_info\":null,\"video_begin\":0,\"video_length\":0,\"tts_link\":\"https://cdn2.toon.at/asset/__special_tts_cache__/Ea8cqSOV96LP7G4CmsmxoPNaUpq0S6SWsDjnexI8gSLfYdOBaBwFS-iV_yXA_EAUJmmgVtEjb77URLGfFWv2ag.mp4\"}}";
        ToonationPayload donation = gson.fromJson(payload, ToonationPayload.class);
        Assertions.assertEquals(donation.getContent().getAccount(), "jjangjjangman");
        Assertions.assertEquals(donation.getContent().getAmount(), 1000L);
        Assertions.assertEquals(donation.getContent().getName(), "짱짱맨");
        Assertions.assertEquals(donation.getContent().getMessage(), "내 돈을 받아라");
    }

    @Test
    void chzzkSerialize() {
        String payload = "{\"svcid\":\"game\",\"ver\":\"1\",\"bdy\":[{\"svcid\":\"game\",\"cid\":\"N1AQgn\",\"mbrCnt\":1439,\"uid\":\"4a0eea4611d302a478d152a4c75dd3cb\",\"profile\":\"{\\\"userIdHash\\\":\\\"4a0eea4611d302a478d152a4c75dd3cb\\\",\\\"nickname\\\":\\\"짱짱맨\\\",\\\"profileImageUrl\\\":\\\"\\\",\\\"userRoleCode\\\":\\\"common_user\\\",\\\"badge\\\":null,\\\"title\\\":null,\\\"verifiedMark\\\":false,\\\"activityBadges\\\":[{\\\"badgeNo\\\":846465,\\\"badgeId\\\":\\\"donation_accumulate_amount_lv1\\\",\\\"imageUrl\\\":\\\"https://ssl.pstatic.net/static/nng/glive/icon/cheese01.png\\\",\\\"activated\\\":true}],\\\"streamingProperty\\\":{}}\",\"msg\":\"내 돈을 받아라\",\"msgTypeCode\":10,\"msgStatusType\":\"NORMAL\",\"extras\":\"{\\\"emojis\\\":{},\\\"isAnonymous\\\":false,\\\"payType\\\":\\\"CURRENCY\\\",\\\"payAmount\\\":1000,\\\"streamingChannelId\\\":\\\"5d53f8fa5bef9b1bd4dc884f9907c079\\\",\\\"nickname\\\":\\\"짱짱맨\\\",\\\"osType\\\":\\\"PC\\\",\\\"donationType\\\":\\\"CHAT\\\",\\\"weeklyRankList\\\":[{\\\"userIdHash\\\":\\\"bc0bb4421437a81507779821c36a2c1e\\\",\\\"nickName\\\":\\\"혜윰o\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":500000,\\\"ranking\\\":1},{\\\"userIdHash\\\":\\\"0ae3723f45fb4afe007029335095e29e\\\",\\\"nickName\\\":\\\"이솔보리\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":31000,\\\"ranking\\\":2},{\\\"userIdHash\\\":\\\"0bc390a8bbcde435f771bf22e04b0621\\\",\\\"nickName\\\":\\\"덴경대\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":23000,\\\"ranking\\\":3},{\\\"userIdHash\\\":\\\"e4d99ed5689a3ed68d57458854ae8f86\\\",\\\"nickName\\\":\\\"빡포션\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":18000,\\\"ranking\\\":4},{\\\"userIdHash\\\":\\\"3e3f275d6df594dbdc4f907dee40e381\\\",\\\"nickName\\\":\\\"미분\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":13000,\\\"ranking\\\":5},{\\\"userIdHash\\\":\\\"ca694b049b68b670432b657a5877680d\\\",\\\"nickName\\\":\\\"불명예훈장\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":11000,\\\"ranking\\\":6},{\\\"userIdHash\\\":\\\"4a0eea4611d302a478d152a4c75dd3cb\\\",\\\"nickName\\\":\\\"짱짱맨\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":10000,\\\"ranking\\\":7},{\\\"userIdHash\\\":\\\"2e70454ccacb2f778c72189ef5f82c83\\\",\\\"nickName\\\":\\\"동동꾸리\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":6000,\\\"ranking\\\":8},{\\\"userIdHash\\\":\\\"f21497032ad21c4fb54cb2fed8bceec3\\\",\\\"nickName\\\":\\\"라면먹는그브\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":5000,\\\"ranking\\\":9},{\\\"userIdHash\\\":\\\"b9b4e5f489b0a3706ede4976abe0eaf9\\\",\\\"nickName\\\":\\\"시 도\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":5000,\\\"ranking\\\":10}],\\\"donationUserWeeklyRank\\\":{\\\"userIdHash\\\":\\\"4a0eea4611d302a478d152a4c75dd3cb\\\",\\\"nickName\\\":\\\"짱짱맨\\\",\\\"verifiedMark\\\":false,\\\"donationAmount\\\":10000,\\\"ranking\\\":7},\\\"chatType\\\":\\\"STREAMING\\\"}\",\"ctime\":1714752654401,\"utime\":1714752654401,\"msgTid\":null,\"msgTime\":1714752654401}],\"cmd\":93102,\"tid\":null,\"cid\":\"N1AQgn\"}";
        ChzzkPayload donation = gson.fromJson(payload, ChzzkPayload.class);
        Assertions.assertEquals(donation.getBody().getProfile().getUserIdHash(), "4a0eea4611d302a478d152a4c75dd3cb");
        Assertions.assertEquals(donation.getBody().getExtras().getPayAmount(), 1000L);
        Assertions.assertEquals(donation.getBody().getProfile().getNickname(), "짱짱맨");
        Assertions.assertEquals(donation.getBody().getMsg(), "내 돈을 받아라");
    }
}