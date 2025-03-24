package com.choongang.auction.streamingauction.domain.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ChatRequestDto(
        Long memberId,
        String nickName,
        Long auctionId,
        String message,

        // 백엔드에서 LocalDateTime을 제대로 처리할 수 있도록 'yyyy-MM-dd HH:mm:ss.SSS' 형식으로 처리
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        LocalDateTime sentAt
) {
}
