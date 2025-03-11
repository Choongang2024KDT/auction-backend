package com.choongang.auction.streamingauction.domain.dto.requestDto;

public record ChatRequestDto(
        Long userId,
        Long auctionId,
        String message
) {
}
