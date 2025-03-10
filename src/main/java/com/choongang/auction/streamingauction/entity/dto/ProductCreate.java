package com.choongang.auction.streamingauction.entity.dto;


import com.choongang.auction.streamingauction.entity.Product;
import java.time.LocalDateTime;

//상품 등록시 사용할 DTO
public record ProductCreate(
       String productName,  // 상품명
       String productDescription,  // 상품 설명
       String productCategory,  // 상품 카테고리
       String productImageUrl  // 상품 이미지 URL
) {
    //DTO를 엔터티로 변경하는 편의 메서드
    public Product toEntity() {
        return Product.builder()
                .name(this.productName)
                .description(this.productDescription)
                .category(this.productCategory)
                .imageUrl(this.productImageUrl)
//                .createdAt(LocalDateTime.now())
                .build();
    }

}
