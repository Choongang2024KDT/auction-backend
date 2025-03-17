package com.choongang.auction.streamingauction.domain.product.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {
    private Long id;
    private String imageUrl;
    private Long productId;
    private LocalDateTime uploadedAt;
}