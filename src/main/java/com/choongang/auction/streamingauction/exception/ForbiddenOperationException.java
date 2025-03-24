package com.choongang.auction.streamingauction.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ForbiddenOperationException extends RuntimeException {

    private final HttpStatus status;

    public ForbiddenOperationException(String message) {
        super(message);
        this.status = HttpStatus.FORBIDDEN;
    }

}