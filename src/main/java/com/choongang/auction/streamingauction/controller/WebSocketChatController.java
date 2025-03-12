package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.requestDto.ChatRequestDto;
import com.choongang.auction.streamingauction.service.ChatService;
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
public class WebSocketChatController {

    private final ChatService chatService;

    // 채팅 메시지 처리
    // "/auction/{auctionId}/chat" 경로로 채팅 메시지를 받음
    @MessageMapping("/{auctionId}/chat")
    @SendTo("/topic/chat")
    public void handleChat(@DestinationVariable Long auctionId , ChatRequestDto chatRequestDto) {

        // 메시지 로그 찍기
        log.info("Received message for auctionId: {}, ChatRequestDto: {}", auctionId, chatRequestDto);


        // 채팅 메시지 처리 로직 (메시지 저장, 경매 채팅방에 전송 등)
        chatService.saveAndSendMessage(chatRequestDto); //채팅내역 저장 후 클라이언트에게 내역 전송
    }

}
