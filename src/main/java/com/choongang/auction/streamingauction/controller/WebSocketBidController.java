package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.requestDto.BidRequestDto;
import com.choongang.auction.streamingauction.domain.dto.responseDto.BidResponseDto;
import com.choongang.auction.streamingauction.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebSocketBidController {

    private final BidService bidService;

    // 웹소켓을 통해 최고 입찰가 전송
    @MessageMapping("/auction/bid")
    public void handleBid(@DestinationVariable BidRequestDto bidRequestDto) {
        // 입찰 처리 로직 (경매의 최고 입찰가 업데이트 등)

        // 최고 입찰가 조회
        bidService.saveAndGetMaxBid(bidRequestDto);

    }
}


