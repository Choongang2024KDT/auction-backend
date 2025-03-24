package com.choongang.auction.streamingauction.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuctionNotFoundException extends RuntimeException {

    private final HttpStatus status;

    public AuctionNotFoundException(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
    }

}