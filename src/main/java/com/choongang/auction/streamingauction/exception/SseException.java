package com.choongang.auction.streamingauction.exception;

import lombok.Getter;

@Getter
public class SseException extends RuntimeException {
    private final ErrorCode errorCode;

    public SseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public SseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}