package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.requestDto.BidRequestDto;
import com.choongang.auction.streamingauction.domain.dto.responseDto.BidResponseDto;
import com.choongang.auction.streamingauction.service.BidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WebSocketBidController {

    private final BidService bidService;

    // 웹소켓을 통해 최고 입찰가 전송
    // 입찰 데이터 처리
    // "/auction/{auctionId}/bid" 경로로 입찰 데이터 받음
    @MessageMapping("/{auctionId}/bid")
    @SendTo("/topic/bid")
    public void handleBid(@DestinationVariable Long auctionId , BidRequestDto bidRequestDto) {
        // 메시지 로그 찍기
        log.info("Received message for auctionId: {}, ChatRequestDto: {}", auctionId, bidRequestDto);

        // 입찰 데이터 저장 후 최고 입찰가 조회
        bidService.saveAndGetMaxBid(bidRequestDto);

    }
}


