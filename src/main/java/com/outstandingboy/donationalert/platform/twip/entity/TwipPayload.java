package com.outstandingboy.donationalert.platform.twip.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class TwipPayload {
    @SerializedName("watcher_id")
    private String watcherId;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("amount")
    private long amount;
    @SerializedName("comment")
    private String comment;
}
