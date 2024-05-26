package com.outstandingboy.donationalert.common.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class Donation {
    private String id;
    private String nickName;
    private String comment;
    private long amount;
}
