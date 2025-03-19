package com.choongang.auction.streamingauction.domain.dto.responseDto;

import com.choongang.auction.streamingauction.domain.entity.Status;
import com.choongang.auction.streamingauction.domain.product.domain.dto.ProductDTO;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AuctionResponseDto(
        Long id,
        ProductDTO product,  // Entity 대신 DTO 사용
        Long currentPrice,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Status status,
        boolean success,       // 추가된 필드: 성공 여부
        String message         // 추가된 필드: 메시지
) {
}

