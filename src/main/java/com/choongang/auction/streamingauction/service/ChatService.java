package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.requestDto.ChatRequestDto;
import com.choongang.auction.streamingauction.domain.entity.Chat;
import com.choongang.auction.streamingauction.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;

    //채팅 저장 기능
    public void saveMessage(ChatRequestDto dto){
        Chat chatEntity = Chat.builder()
                .userId(dto.userId())
                .auctionId(dto.auctionId())
                .message(dto.message())
                .build();
        chatRepository.save(chatEntity);
    }

    //채팅 내역 조회 - 채팅 시간 순
    public List<Chat> getChat(Long auctionId){
        return chatRepository.findByAuctionIdOrderBySentAtAsc(auctionId);

    }
}
