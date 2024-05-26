package com.outstandingboy.donationalert.platform.toonation;

import com.outstandingboy.donationalert.common.entity.Donation;
import com.outstandingboy.donationalert.common.event.Topic;
import com.outstandingboy.donationalert.common.exception.TokenNotFoundException;
import com.outstandingboy.donationalert.common.util.Gsons;
import com.outstandingboy.donationalert.platform.Platform;
import com.outstandingboy.donationalert.platform.toonation.entity.ToonationPayload;
import lombok.Getter;
import lombok.SneakyThrows;
import okhttp3.*;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Toonation extends WebSocketListener implements Platform, Closeable {
    private final static Pattern PAYLOAD_PATTERN = Pattern.compile("\"payload\":\"(.*)\",");
    private final OkHttpClient client;
    private final @Getter String key;
    private final @Getter String payload;
    private WebSocket socket;
    private final @Getter Topic<Donation> donationTopic;
    private final @Getter Topic<String> messageTopic;
    private boolean timeout = false;

    public Toonation(String key) {
        this.key = key;
        this.client = new OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build();

        this.payload = getPayload(key);

        if (payload == null) {
            throw new TokenNotFoundException("투네이션 페이로드를 찾을 수 없습니다.");
        }

        Request request = new Request.Builder()
            .url("wss://toon.at:8071/" + payload)
            .build();

        socket = client.newWebSocket(request, this);
        client.dispatcher().executorService().shutdown();

        donationTopic = new Topic<>();
        messageTopic = new Topic<>();
    }

    @SneakyThrows
    private String getPayload(String key) {
        Request request = new Request.Builder()
            .url("https://toon.at/widget/alertbox/" + key)
            .get()
            .build();
        Response res = client.newCall(request).execute();
        if (res.isSuccessful()) {
            String body = res.body().string();
            Matcher m = PAYLOAD_PATTERN.matcher(body);
            if (m.find()) {
                return m.group(1);
            }
        }
        return null;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        if (!timeout)
            messageTopic.publish("투네이션에 연결되었습니다!");
        else {
            timeout = false;
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        ToonationPayload payload = Gsons.gson().fromJson(text, ToonationPayload.class);
        if (payload.getContent() == null) {
            return;
        }
        Donation donation = Donation.builder()
            .id(payload.getContent().getAccount())
            .comment(payload.getContent().getMessage())
            .nickName(payload.getContent().getName())
            .amount(payload.getContent().getAmount())
            .build();
        if (donation.getId() != null) {
            donationTopic.publish(donation);
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        messageTopic.publish("투네이션 연결이 종료 되었습니다!");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        timeout = true;

        webSocket.close(1000, null);

        OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build();
        Request request = new Request.Builder()
            .url("wss://toon.at:8071/" + payload)
            .build();

        socket = client.newWebSocket(request, this);
    }

    @Override
    public void close() {
        messageTopic.close();
        donationTopic.close();
        socket.close(1000, null);
    }
}
