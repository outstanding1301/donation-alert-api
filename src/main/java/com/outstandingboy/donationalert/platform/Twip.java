package com.outstandingboy.donationalert.platform;

import com.outstandingboy.donationalert.entity.Donation;
import com.outstandingboy.donationalert.exception.TokenNotFoundException;
import com.outstandingboy.donationalert.exception.TwipVersionNotFoundException;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Twip implements Platform {
    private Socket socket = null;
    private Subject<Donation> donationObservable;
    private Subject<String> messageObservable;

    public Twip(String key) throws IOException {
        Document doc = Jsoup.connect("https://twip.kr/widgets/alertbox/"+key).get();
        Elements scriptElements = doc.getElementsByTag("script");
        String script = scriptElements.stream().filter(e -> !e.hasAttr("src")).map(Element::toString).collect(Collectors.joining());

        String version = parseVersion(script);
        String token = parseToken(script);

        if (version == null) {
            throw new TwipVersionNotFoundException("버전을 찾을 수 없습니다.");
        }
        if (token == null) {
            throw new TokenNotFoundException("토큰을 찾을 수 없습니다.");
        }

        init(key, version, token);
    }

    public Twip(String key, String version, String token) {
        init(key, version, token);
    }

    private void init(String key, String version, String token) {
        String uri = String.format("https://io.mytwip.net?alertbox_key="+key
                +"&version="+version+"&token="+encodeURIComponent(token));

        IO.Options opts = new IO.Options();
        String transports[] = {"websocket", "polling"};
        opts.transports = transports;
        opts.reconnection = true;

        try {
            socket = IO.socket(uri, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT, (args) -> {
            messageObservable.onNext("트윕에 연결되었습니다!");
        })
        .on(Socket.EVENT_CONNECT_ERROR, (args) -> {
            messageObservable.onNext("연결 오류가 발생했습니다.");
        })
        .on(Socket.EVENT_ERROR, (args) -> {
            messageObservable.onNext("오류가 발생했습니다.");
        })
        .on("disconnect", (args) -> {
            messageObservable.onNext("연결이 종료되었습니다.");
        })
        .on("version not match", (args) -> {
            messageObservable.onNext("트윕 버전이 일치하지 않습니다.");
        })
        .on("not allowed ip", (args) -> {
            messageObservable.onNext("허용되지 않은 IP입니다.");
        })
        .on("new donate", (args) -> {
            JSONParser parser = new JSONParser();
            try {
                JSONObject json = (JSONObject) parser.parse(args[0].toString());
                Donation donation = getDonation(json);

                if(donation != null) {
                    donationObservable.onNext(donation);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        socket.connect();

        donationObservable = PublishSubject.create();
        messageObservable = PublishSubject.create();
    }

    private String parseVersion(String script) {
        Pattern p = Pattern.compile("version: '(.*)'");
        Matcher m = p.matcher(script);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
    private String parseToken(String script) {
        Pattern p = Pattern.compile("window.__TOKEN__ = '(.*)'");
        Matcher m = p.matcher(script);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private Donation getDonation(JSONObject json){
        try {
            Donation donation = new Donation();
            donation.setId((String) json.get("watcher_id"));
            donation.setNickName((String) json.get("nickname"));
            donation.setAmount((long) json.get("amount"));
            donation.setComment((String) json.get("comment"));
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
        socket.close();
    }

    public static String encodeURIComponent(String s)
    {
        String result = null;
        try {
            result = URLEncoder.encode(s, "UTF-8").replaceAll("%", "%%");
        }
        catch (UnsupportedEncodingException e) {
            result = s;
        }
        return result;
    }
}
