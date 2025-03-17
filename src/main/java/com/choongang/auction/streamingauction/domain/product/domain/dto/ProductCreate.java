package com.choongang.auction.streamingauction.domain.product.domain.dto;

import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import java.math.BigDecimal;

// 상품 등록시 사용할 DTO
public record ProductCreate(
        String productName,
        String productDescription,
        String productCategory,  // 카테고리 타입 이름(String)
        Long productStartPrice,  // 시작가
        Long productBidIncrement, // 입찰 단위
        Long productBuyNowPrice,  // 즉시구매가
        String imageUrl // 이미지 URL
) {

    // DTO를 엔터티로 변경하는 편의 메서드 - 가격 정보를 포함
    public Product toEntity() {
        return Product.builder()
                .name(this.productName())
                .description(this.productDescription())
                // 가격 관련 필드 통합
                .startPrice(BigDecimal.valueOf(this.productStartPrice()))
                .bidIncrease(BigDecimal.valueOf(this.productBidIncrement()))
                .buyNowPrice(BigDecimal.valueOf(this.productBuyNowPrice()))
                .build();
    }
}