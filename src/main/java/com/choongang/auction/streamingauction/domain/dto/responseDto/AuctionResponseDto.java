package com.choongang.auction.streamingauction.domain.dto.responseDto;

import com.choongang.auction.streamingauction.domain.entity.Status;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AuctionResponseDto(
        Long id,
        Product product,
        Long userId,
        Long currentPrice,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Status status
) {
}
