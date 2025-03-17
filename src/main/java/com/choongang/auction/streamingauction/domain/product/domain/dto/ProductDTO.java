package com.choongang.auction.streamingauction.domain.product.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;
    private String name;
    private String description;
    private BigDecimal startingPrice;
    private BigDecimal bidIncrease;
    private BigDecimal buyNowPrice;
    private String categoryType;
    private String sellerUsername;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}