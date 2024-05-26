package com.outstandingboy.donationalert.platform.twip;

import com.outstandingboy.donationalert.common.entity.Donation;
import com.outstandingboy.donationalert.common.entity.Message;
import com.outstandingboy.donationalert.common.event.Topic;
import com.outstandingboy.donationalert.common.exception.TokenNotFoundException;
import com.outstandingboy.donationalert.common.util.Gsons;
import com.outstandingboy.donationalert.platform.Platform;
import com.outstandingboy.donationalert.platform.twip.entity.TwipPayload;
import com.outstandingboy.donationalert.platform.twip.error.TwipVersionNotFoundException;
import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.Getter;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Twip implements Platform, Closeable {
    private final static Pattern VERSION_PATTERN = Pattern.compile("version: '(.*)'");
    private final static Pattern TOKEN_PATTERN = Pattern.compile("window.__TOKEN__ = '(.*)'");
    private Socket socket;
    private final OkHttpClient client;
    private final @Getter Topic<Donation> donationTopic;
    private final @Getter Topic<Message> messageTopic;
    private final @Getter Topic<String> legacyMessageTopic;
    private final @Getter String key;
    private @Getter String version;
    private @Getter String token;

    public Twip(String key) {
        this(key, new OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build(), new Topic<>(), new Topic<>(), new Topic<>());
    }

    public Twip(String key, OkHttpClient client, Topic<Donation> donationTopic, Topic<Message> messageTopic, Topic<String> legacyMessageTopic) {
        this.key = key;
        this.client = client;
        this.donationTopic = donationTopic;
        this.messageTopic = messageTopic;
        this.legacyMessageTopic = legacyMessageTopic;
        init();
    }

    private void init() {
        initVersionAndToken(key);
        initSocket();
    }

    private void initSocket() {
        String uri = String.format("https://io.mytwip.net?alertbox_key=%s&version=%s&token=%s",
            key, version, encodeURIComponent(token));

        IO.Options opts = new IO.Options();
        opts.transports = new String[]{"websocket", "polling"};
        opts.reconnection = true;

        socket = IO.socket(URI.create(uri), opts);

        socket.on(Socket.EVENT_CONNECT, (args) -> {
                legacyMessageTopic.publish("트윕에 연결되었습니다!");
                messageTopic.publish(new Message(Message.Key.TWIP_CONNECT, args));
            })
            .on(Socket.EVENT_CONNECT_ERROR, (args) -> {
                legacyMessageTopic.publish("연결 오류가 발생했습니다.");
                messageTopic.publish(new Message(Message.Key.TWIP_CONNECT_ERROR, args));
            })
            .on(Socket.EVENT_ERROR, (args) -> {
                legacyMessageTopic.publish("오류가 발생했습니다.");
                messageTopic.publish(new Message(Message.Key.TWIP_ERROR, args));
            })
            .on(Socket.EVENT_DISCONNECT, (args) -> {
                legacyMessageTopic.publish("연결이 종료되었습니다.");
                messageTopic.publish(new Message(Message.Key.TWIP_DISCONNECT, args));
            })
            .on("version not match", (args) -> {
                legacyMessageTopic.publish("트윕 버전이 일치하지 않습니다.");
                messageTopic.publish(new Message(Message.Key.TWIP_VERSION_NOT_MATCH, args));
            })
            .on("not allowed ip", (args) -> {
                legacyMessageTopic.publish("허용되지 않은 IP입니다.");
                messageTopic.publish(new Message(Message.Key.TWIP_NOT_ALLOWED_IP, args));
            })
            .on("new donate", (args) -> {
                TwipPayload payload = Gsons.gson().fromJson(args[0].toString(), TwipPayload.class);
                Donation donation = Donation.builder()
                    .id(payload.getWatcherId())
                    .comment(payload.getComment())
                    .nickName(payload.getNickname())
                    .amount(payload.getAmount())
                    .build();
                if (donation.getId() != null) {
                    donationTopic.publish(donation);
                }
                messageTopic.publish(new Message(Message.Key.TWIP_CONNECT, args));
            });
        socket.connect();
    }

    private void initVersionAndToken(String key) {
        Request request = new Request.Builder()
            .url("https://twip.kr/widgets/alertbox/" + key)
            .get()
            .build();

        try (Response res = client.newCall(request).execute()) {
            if (res.isSuccessful()) {
                String body = res.body().string();
                this.version = parseVersion(body);
                if (version == null) {
                    throw new TwipVersionNotFoundException("버전을 찾을 수 없습니다.");
                }
                this.token = parseToken(body);
                if (token == null) {
                    throw new TokenNotFoundException("토큰을 찾을 수 없습니다.");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String parseVersion(String script) {
        Matcher m = VERSION_PATTERN.matcher(script);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private static String parseToken(String script) {
        Matcher m = TOKEN_PATTERN.matcher(script);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    @Override
    public void close() {
        donationTopic.close();
        legacyMessageTopic.close();
        socket.close();
    }

    @SneakyThrows
    public static String encodeURIComponent(String s) {
        return URLEncoder.encode(s, "UTF-8");
    }
}
