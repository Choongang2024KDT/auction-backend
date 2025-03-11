package com.choongang.auction.streamingauction.product.domain.dto;

import com.choongang.auction.streamingauction.product.domain.entity.Product;

// 상품 등록시 사용할 DTO
public record ProductCreate(
        String productName,
        String productDescription,
        String productCategory,
        String imageUrl // 이미지 URL을 문자열로 변경
) {
    // DTO를 엔터티로 변경하는 편의 메서드
    public Product toEntity() {
        return Product.builder()
                .name(this.productName())
                .description(this.productDescription())
                .category(this.productCategory())
                .build(); // 이미지는 별도 처리
    }
}