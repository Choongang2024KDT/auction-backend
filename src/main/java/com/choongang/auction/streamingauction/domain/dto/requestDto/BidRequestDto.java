package com.choongang.auction.streamingauction.domain.dto.requestDto;

import com.choongang.auction.streamingauction.domain.entity.Auction;

//입찰 요청 dto - 해당 구매자의 id , 현재 진행중인 경매의 id , 입찰가
public record BidRequestDto(
        Long userId,
        Long auctionId,
        Long bidAmount
) {
}
