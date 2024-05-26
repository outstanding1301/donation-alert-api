package com.outstandingboy.donationalert.platform.twip;

import com.outstandingboy.donationalert.common.entity.Donation;
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
    private final @Getter Topic<String> messageTopic;

    private final @Getter String key;
    private final @Getter String version;
    private final @Getter String token;

    @SneakyThrows
    public Twip(String key) {
        this.key = key;
        this.client = new OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build();
        String[] versionAndToken = initVersionAndToken(key);
        this.version = versionAndToken[0];
        this.token = versionAndToken[1];

        if (version == null) {
            throw new TwipVersionNotFoundException("버전을 찾을 수 없습니다.");
        }
        if (token == null) {
            throw new TokenNotFoundException("토큰을 찾을 수 없습니다.");
        }

        donationTopic = new Topic<>();
        messageTopic = new Topic<>();

        init(key, version, token);
    }

    private void init(String key, String version, String token) {
        String uri = String.format("https://io.mytwip.net?alertbox_key=%s&version=%s&token=%s",
            key, version, encodeURIComponent(token));

        IO.Options opts = new IO.Options();
        opts.transports = new String[]{"websocket", "polling"};
        opts.reconnection = true;

        socket = IO.socket(URI.create(uri), opts);

        socket.on(Socket.EVENT_CONNECT, (args) -> {
                messageTopic.publish("트윕에 연결되었습니다!");
            })
            .on(Socket.EVENT_CONNECT_ERROR, (args) -> {
                messageTopic.publish("연결 오류가 발생했습니다."); // TODO: ErrorTopic
            })
            .on(Socket.EVENT_ERROR, (args) -> {
                messageTopic.publish("오류가 발생했습니다."); // TODO: ErrorTopic
            })
            .on("disconnect", (args) -> {
                messageTopic.publish("연결이 종료되었습니다.");
            })
            .on("version not match", (args) -> {
                messageTopic.publish("트윕 버전이 일치하지 않습니다."); // TODO: ErrorTopic
            })
            .on("not allowed ip", (args) -> {
                messageTopic.publish("허용되지 않은 IP입니다."); // TODO: ErrorTopic
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
            });
        socket.connect();
    }

    @SneakyThrows
    private String[] initVersionAndToken(String key) {
        Request request = new Request.Builder()
            .url("https://twip.kr/widgets/alertbox/" + key)
            .get()
            .build();
        String[] versionAndToken = new String[2];
        Response res = client.newCall(request).execute();
        if (res.isSuccessful()) {
            String body = res.body().string();
            versionAndToken[0] = parseVersion(body);
            versionAndToken[1] = parseToken(body);
        }
        return versionAndToken;
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
        messageTopic.close();
        socket.close();
    }

    @SneakyThrows
    public static String encodeURIComponent(String s) {
        return URLEncoder.encode(s, "UTF-8");
    }
}
