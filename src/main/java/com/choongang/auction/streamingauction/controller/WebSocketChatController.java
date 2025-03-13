package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.requestDto.ChatRequestDto;
import com.choongang.auction.streamingauction.domain.entity.Chat;
import com.choongang.auction.streamingauction.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WebSocketChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    // 채팅 메시지 처리
    // "/auction/{auctionId}/chat" 경로로 채팅 메시지를 받음
    @MessageMapping("/{auctionId}/chat")
    @SendTo("/topic/chat")
    public void handleChat(@DestinationVariable Long auctionId , ChatRequestDto chatRequestDto) {

        // 메시지 로그 찍기
        log.info("Received message for auctionId: {}, ChatRequestDto: {}", auctionId, chatRequestDto);


        // 채팅 메시지 처리 로직 (메시지 저장, 경매 채팅방에 전송 등)
        chatService.saveAndSendMessage(chatRequestDto);//채팅내역 저장
        // 2. 저장한 메시지를 웹소켓을 통해 다른 클라이언트들에게 전달
        // WebSocket으로 채팅 메시지를 "/topic/chat"로 전송
        // 모든 클라이언트에게 전송
        messagingTemplate.convertAndSend("/topic/chat", chatRequestDto);

    }

}
