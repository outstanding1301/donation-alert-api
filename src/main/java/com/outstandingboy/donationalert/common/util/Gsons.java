package com.outstandingboy.donationalert.common.util;

import com.google.gson.Gson;

public class Gsons {
    private static Gson gson = new Gson();

    public static Gson gson() {
        return gson;
    }
}
