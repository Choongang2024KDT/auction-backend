package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.requestDto.ChatRequestDto;
import com.choongang.auction.streamingauction.domain.entity.Auction;
import com.choongang.auction.streamingauction.domain.entity.Chat;
import com.choongang.auction.streamingauction.repository.AuctionRepository;
import com.choongang.auction.streamingauction.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;
    private final AuctionRepository auctionRepository;

    //웹소켓으로 받은 채팅내역 저장후 클라이언트에게 보여주기
    public void saveAndSendMessage(ChatRequestDto dto) {
        // 1. 채팅 메시지를 DB에 저장
        //해당 경매 찾기
        Auction foundAuction = auctionRepository.findById(dto.auctionId()).orElseThrow(() -> new RuntimeException("Auction not found"));

        Chat chatEntity = Chat.builder()
                .userId(dto.userId())
                .auction(foundAuction)
                .message(dto.message())
                .build();
        chatRepository.save(chatEntity);

        // 2. 저장한 메시지를 웹소켓을 통해 다른 클라이언트들에게 전달
        // WebSocket으로 채팅 메시지를 "/topic/chat/{auctionId}"로 전송
        messagingTemplate.convertAndSend("/topic/chat/" + dto.auctionId(), chatEntity);
    }


    //채팅 저장 기능
    public void saveMessage(ChatRequestDto dto){

        //해당 경매 찾기
        Auction foundAuction = auctionRepository.findById(dto.auctionId()).orElseThrow(() -> new RuntimeException("Auction not found"));

        Chat chatEntity = Chat.builder()
                .userId(dto.userId())
                .auction(foundAuction)
                .message(dto.message())
                .build();
        chatRepository.save(chatEntity);
    }

    //채팅 내역 조회 - 채팅 시간 순
    public List<Chat> getChat(Long auctionId){
        return chatRepository.findByAuctionIdOrderBySentAtAsc(auctionId);
    }
}
