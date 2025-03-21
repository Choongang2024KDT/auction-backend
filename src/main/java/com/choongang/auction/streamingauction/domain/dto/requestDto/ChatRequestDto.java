package com.choongang.auction.streamingauction.domain.dto.requestDto;

public record ChatRequestDto(
        Long memberId,
        String nickName,
        Long auctionId,
        String message,
        String timestamp // 타임스탬프 추가
) {
}
