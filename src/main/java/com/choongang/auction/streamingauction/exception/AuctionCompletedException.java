package com.choongang.auction.streamingauction.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuctionCompletedException extends RuntimeException {

    private final HttpStatus status;

    public AuctionCompletedException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }
}
