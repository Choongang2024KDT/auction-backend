package com.choongang.auction.streamingauction.domain.dto.responseDto;

import com.choongang.auction.streamingauction.domain.entity.Auction;

public record BidResponseDto(
        String name,
        Long bidAmount
) {
}
