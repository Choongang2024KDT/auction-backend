package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.requestDto.BidRequestDto;
import com.choongang.auction.streamingauction.domain.dto.responseDto.BidResponseDto;
import com.choongang.auction.streamingauction.domain.entity.Bid;
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

    private final SimpMessagingTemplate messagingTemplate;
    private final BidService bidService;

    // 웹소켓을 통해 최고 입찰가 전송
    // 입찰 데이터 처리
    // "/auction/{auctionId}/bid" 경로로 입찰 데이터 받음
    @MessageMapping("/{auctionId}/bid")
    @SendTo("/topic/bid/{auctionId}") // 요청 받은 데이터 자동으로 반환 할 수 있음
    public void handleBid(@DestinationVariable Long auctionId , BidRequestDto bidRequestDto) {
        // 메시지 로그 찍기
        log.info("Received message for auctionId: {}, ChatRequestDto: {}", auctionId, bidRequestDto);

        // 입찰 데이터 저장 후 최고 입찰가 조회
        BidResponseDto MaxBidInfo = bidService.saveAndGetMaxBid(bidRequestDto);

        // WebSocket으로 입찰 정보를 "/topic/bid"로 전송
        // 서버가 직접 클라이언트에게 메세지를 보낼 수 있음 (SendTo랑 사용목적은 비슷하지만 입맛대로 데이터를 보낼 수 있음)
        // 둘 중 하나만 선택해서 사용해도 됨
        messagingTemplate.convertAndSend("/topic/bid/"  + auctionId , MaxBidInfo);
    }
}


