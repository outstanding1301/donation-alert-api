package com.outstandingboy.donationalert.platform.toonation.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class ToonationPayload {
    @Getter
    public static class Content {
        @SerializedName("account")
        private String account;
        @SerializedName("name")
        private String name;
        @SerializedName("amount")
        private long amount;
        @SerializedName("message")
        private String message;
    }
    private Content content;
}
