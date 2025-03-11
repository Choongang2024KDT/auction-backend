package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.requestDto.ChatRequestDto;
import com.choongang.auction.streamingauction.domain.entity.Chat;
import com.choongang.auction.streamingauction.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/chat")
public class ChatController {
    private final ChatService chatService;

    //채팅 저장 요청 처리
    @PostMapping("/saveText")
    public ResponseEntity<?> saveChat(
            @RequestBody ChatRequestDto chatRequestDto
    ) {
        chatService.saveMessage(chatRequestDto);
        return ResponseEntity.ok().body(Map.of(
                "message" , "채팅을 저장했습니다."
        ));
    }

    //채팅 조회 요청
    @GetMapping("/{auctionId}") //경매방의 url에 따라 몇번경매방 채팅테이블인지 구분
    public ResponseEntity<?> getChat(
            @PathVariable Long auctionId
    ){
        // 해당 경매에 대한 채팅 내역을 조회
        List<Chat> chat = chatService.getChat(auctionId);

        // 메시지에 auctionId를 포함시켜 반환
        String message = String.format("%d번 경매방의 채팅 내역입니다", auctionId);

        return ResponseEntity.ok().body(Map.of(
                "message" , message,
                "chat" , chat
        ));
    }
}
