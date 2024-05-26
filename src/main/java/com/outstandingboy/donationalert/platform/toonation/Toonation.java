package com.outstandingboy.donationalert.platform.toonation;

import com.outstandingboy.donationalert.common.entity.Donation;
import com.outstandingboy.donationalert.common.entity.Message;
import com.outstandingboy.donationalert.common.event.Topic;
import com.outstandingboy.donationalert.common.exception.TokenNotFoundException;
import com.outstandingboy.donationalert.common.util.Gsons;
import com.outstandingboy.donationalert.platform.Platform;
import com.outstandingboy.donationalert.platform.toonation.entity.ToonationPayload;
import lombok.Getter;
import okhttp3.*;

import java.io.Closeable;
import java.io.IOException;
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
    private final @Getter Topic<Message> messageTopic;
    private final @Getter Topic<String> legacyMessageTopic;
    private boolean timeout = false;

    public Toonation(String key) {
        this(key, new OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build(), new Topic<>(), new Topic<>(), new Topic<>());
    }

    public Toonation(String key, OkHttpClient client, Topic<Donation> donationTopic, Topic<Message> messageTopic, Topic<String> legacyMessageTopic) {
        this.key = key;
        this.client = client;
        this.donationTopic = donationTopic;
        this.messageTopic = messageTopic;
        this.legacyMessageTopic = legacyMessageTopic;
        this.payload = getPayload(key);

        initSocket();
        client.dispatcher().executorService().shutdown();
    }

    private void initSocket() {
        Request request = new Request.Builder()
            .url("wss://toon.at:8071/" + payload)
            .build();

        socket = client.newWebSocket(request, this);
    }

    private String getPayload(String key) {
        Request request = new Request.Builder()
            .url("https://toon.at/widget/alertbox/" + key)
            .get()
            .build();
        try (Response res = client.newCall(request).execute()) {
            if (res.isSuccessful()) {
                String body = res.body().string();
                Matcher m = PAYLOAD_PATTERN.matcher(body);
                if (m.find()) {
                    return m.group(1);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        throw new TokenNotFoundException("투네이션 페이로드를 찾을 수 없습니다.");
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        if (!timeout) {
            legacyMessageTopic.publish("투네이션에 연결되었습니다!");
            messageTopic.publish(new Message(Message.Key.TOONATION_OPEN, null));
        }
        else {
            timeout = false;
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        messageTopic.publish(new Message(Message.Key.TOONATION_MESSAGE, text));

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
        messageTopic.publish(new Message(Message.Key.TOONATION_CLOSED, code));

        legacyMessageTopic.publish("투네이션 연결이 종료 되었습니다!");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        messageTopic.publish(new Message(Message.Key.TOONATION_FAILURE, t));
        timeout = true;
        webSocket.close(1000, null);
        initSocket();
    }

    @Override
    public void close() {
        legacyMessageTopic.close();
        donationTopic.close();
        socket.close(1000, null);
    }
}
