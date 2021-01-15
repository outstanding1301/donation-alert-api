package com.outstandingboy.donationalert.platform;

import com.outstandingboy.donationalert.entity.Donation;
import com.outstandingboy.donationalert.exception.TokenNotFoundException;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Toonation extends WebSocketListener implements Platform {
    private String payload;
    private WebSocket socket;
    private Subject<Donation> donationObservable;
    private Subject<String> messageObservable;
    private boolean timeout = false;

    public Toonation(String key) throws IOException {
        Document doc = Jsoup.connect("https://toon.at/widget/alertbox/"+key).get();
        Elements scriptElements = doc.getElementsByTag("script");
        String script = scriptElements.stream().filter(e -> !e.hasAttr("src")).map(Element::toString).collect(Collectors.joining());

        String payload = parsePayload(script);

        if (payload == null) {
            throw new TokenNotFoundException("투네이션 페이로드를 찾을 수 없습니다.");
        }

        this.payload = payload;
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
        Request request = new Request.Builder()
                .url("wss://toon.at:8071/"+payload)
                .build();

        socket = client.newWebSocket(request, this);
        client.dispatcher().executorService().shutdown();

        donationObservable = PublishSubject.create();
        messageObservable = PublishSubject.create();
    }

    private String parsePayload(String script) {
        Pattern p = Pattern.compile("\"payload\":\"(.*)\",");
        Matcher m = p.matcher(script);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        if(!timeout)
            messageObservable.onNext("투네이션에 연결되었습니다!");
        else{
            timeout = false;
//            System.out.println("투네이션이 다시 연결되었습니다.");
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(text);
            Donation donation = getDonation(json);

            if(donation != null) {
                donationObservable.onNext(donation);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        messageObservable.onNext("투네이션 연결이 종료 되었습니다!");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
//        System.out.println("투네이션 타임아웃, 재연결을 시도합니다.");
        timeout = true;

        webSocket.close(1000, null);

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
        Request request = new Request.Builder()
                .url("wss://toon.at:8071/"+payload)
                .build();

        socket = client.newWebSocket(request, this);
    }

    private Donation getDonation(JSONObject json) {
        try {
            if (!json.containsKey("content")) return null;
            json = (JSONObject) json.get("content");

            Donation donation = new Donation();
            donation.setId((String) json.get("account"));
            donation.setNickName((String) json.get("name"));
            donation.setAmount((long) json.get("amount"));
            donation.setComment((String) json.get("message"));
            return donation;
        }
        catch (Exception e){
            return null;
        }
    }

    public Subject<Donation> getDonationObservable() {
        return donationObservable;
    }

    public Subject<String> getMessageObservable() {
        return messageObservable;
    }

    @Override
    public void subscribeDonation(Consumer<Donation> onNext) {
        donationObservable.subscribe(onNext);
    }

    @Override
    public void subscribeMessage(Consumer<String> onNext) {
        messageObservable.subscribe(onNext);
    }

    public void close() {
        donationObservable.onComplete();
        messageObservable.onComplete();
        socket.close(1000, null);
    }
}
