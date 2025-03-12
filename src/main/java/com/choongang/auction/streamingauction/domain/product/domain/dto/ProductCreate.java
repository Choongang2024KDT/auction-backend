package com.choongang.auction.streamingauction.domain.product.domain.dto;

import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;

// 상품 등록시 사용할 DTO
public record ProductCreate(
        String productName,
        String productDescription,
        String productCategory,  // 카테고리 타입 이름(String)
        Long productStartPrice,
        Long productBidIncrement,
        Long productBuyNowPrice,
        String imageUrl // 이미지 URL을 문자열로 변경
) {
    // DTO를 엔터티로 변경하는 편의 메서드
    public Product toEntity() {
        return Product.builder()
                .name(this.productName())
                .description(this.productDescription())
                .startPrice(this.productStartPrice())
                .bidIncrement(this.productBidIncrement())
                .buyNowPrice(this.productBuyNowPrice())
                .build();
    }
}