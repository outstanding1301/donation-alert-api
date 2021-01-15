package com.outstandingboy.donationalert.exception;

import java.io.IOException;

public class TokenNotFoundException extends IOException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
