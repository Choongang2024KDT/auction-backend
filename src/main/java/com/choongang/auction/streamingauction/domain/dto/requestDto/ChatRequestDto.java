package com.choongang.auction.streamingauction.domain.dto.requestDto;

public record ChatRequestDto(
        Long memberId,
        Long auctionId,
        String message
) {
}
