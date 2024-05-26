package com.outstandingboy.donationalert.platform.toonation;

import com.outstandingboy.donationalert.common.entity.Donation;
import com.outstandingboy.donationalert.common.exception.TokenNotFoundException;
import com.outstandingboy.donationalert.platform.Platform;
import com.outstandingboy.donationalert.platform.toonation.entity.ToonationPayload;
import com.outstandingboy.donationalert.common.util.Gsons;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import lombok.Getter;
import lombok.SneakyThrows;
import okhttp3.*;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Toonation extends WebSocketListener implements Platform {
    private final static Pattern PAYLOAD_PATTERN = Pattern.compile("\"payload\":\"(.*)\",");
    private final OkHttpClient client;
    private final @Getter String key;
    private final @Getter String payload;
    private WebSocket socket;
    @Getter
    private Subject<Donation> donationObservable;
    @Getter
    private Subject<String> messageObservable;
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

        donationObservable = PublishSubject.create();
        messageObservable = PublishSubject.create();
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
            messageObservable.onNext("투네이션에 연결되었습니다!");
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
            donationObservable.onNext(donation);
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        messageObservable.onNext("투네이션 연결이 종료 되었습니다!");
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
    public Disposable subscribeDonation(Consumer<Donation> onNext) {
        return donationObservable.subscribe(onNext);
    }

    @Override
    public Disposable subscribeMessage(Consumer<String> onNext) {
        return messageObservable.subscribe(onNext);
    }

    public void close() {
        donationObservable.onComplete();
        messageObservable.onComplete();
        socket.close(1000, null);
    }
}
