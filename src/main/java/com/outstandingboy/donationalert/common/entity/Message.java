package com.outstandingboy.donationalert.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;

@Getter
@AllArgsConstructor
public class Message {
    private Key key;
    private Object value;

    @Getter
    @RequiredArgsConstructor
    public enum Key {
        TWIP_CONNECT,
        TWIP_CONNECT_ERROR,
        TWIP_ERROR,
        TWIP_DISCONNECT,
        TWIP_VERSION_NOT_MATCH,
        TWIP_NOT_ALLOWED_IP,
        TWIP_NEW_DONATE,

        TOONATION_OPEN,
        TOONATION_MESSAGE,
        TOONATION_CLOSED,
        TOONATION_FAILURE,

        CHZZK_OPEN,
        CHZZK_MESSAGE,
        CHZZK_CLOSED,
        CHZZK_FAILURE,
    }
}
