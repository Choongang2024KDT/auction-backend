package com.choongang.auction.streamingauction.exception;


import lombok.Getter;

@Getter
public class PostException extends RuntimeException {

    private final ErrorCode errorCode;

    public PostException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public PostException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
