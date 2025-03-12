package com.choongang.auction.streamingauction.domain.dto.requestDto;

import java.time.LocalDateTime;

public record AuctionRequestDto(
        Long productId,
        Long userId,
        String title,
        String description,
        Long startingPrice
) {
}
