package com.choongang.auction.streamingauction.domain.product.domain.dto;

import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;

// 상품 등록시 사용할 DTO
public record ProductCreate(
        String productName,
        String productDescription,
        String productCategory,  // 카테고리 타입 이름(String)
        Long productStartPrice,  // AuctionBoard의 price로 사용
        Long productBidIncrement, // AuctionBoard의 bid_increase로 사용
        Long productBuyNowPrice,  // AuctionBoard의 buy_now_price로 사용
        String imageUrl // 이미지 URL
) {

    // DTO를 엔터티로 변경하는 편의 메서드 - 가격 정보는 Product에서 제거되었으므로 제외
    public Product toEntity() {
        return Product.builder()
                .name(this.productName())
                .description(this.productDescription())
                // 가격 관련 필드는 제거 (AuctionBoard로 이동)
                // .startPrice(this.productStartPrice())
                // .bidIncrement(this.productBidIncrement())
                // .buyNowPrice(this.productBuyNowPrice())
                .build();
    }
}