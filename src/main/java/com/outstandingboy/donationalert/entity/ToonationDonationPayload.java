package com.outstandingboy.donationalert.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class ToonationDonationPayload {
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
