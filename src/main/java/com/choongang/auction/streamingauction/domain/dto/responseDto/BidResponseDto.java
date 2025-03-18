package com.choongang.auction.streamingauction.domain.dto.responseDto;

import com.choongang.auction.streamingauction.domain.entity.Auction;

public record BidResponseDto(
        Long userId,
        Long bidAmount
) {
}
