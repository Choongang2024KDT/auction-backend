package com.choongang.auction.streamingauction.domain.product.domain.dto;

import com.choongang.auction.streamingauction.domain.dto.responseDto.AuctionResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long productId;
    private String name;
    private Long memberId;
    private String description;
    private Long startingPrice;
    private Long bidIncrease;
    private Long buyNowPrice;
    private String categoryType;
    private String sellerUsername;
    private List<String> imageUrls;
    private AuctionResponseDto auction;
    private Long auctionId;
    private String auctionStatus;
    private Long currentPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}