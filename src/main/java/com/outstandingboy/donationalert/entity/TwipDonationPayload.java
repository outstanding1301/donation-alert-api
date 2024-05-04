package com.outstandingboy.donationalert.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class TwipDonationPayload {
    @SerializedName("watcher_id")
    private String watcherId;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("amount")
    private long amount;
    @SerializedName("comment")
    private String comment;
}
