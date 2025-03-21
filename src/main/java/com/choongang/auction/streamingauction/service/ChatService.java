package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.requestDto.ChatRequestDto;
import com.choongang.auction.streamingauction.domain.dto.responseDto.ChatResponseDto;
import com.choongang.auction.streamingauction.domain.entity.Auction;
import com.choongang.auction.streamingauction.domain.entity.Chat;
import com.choongang.auction.streamingauction.domain.member.entity.Member;
import com.choongang.auction.streamingauction.repository.AuctionRepository;
import com.choongang.auction.streamingauction.repository.ChatRepository;
import com.choongang.auction.streamingauction.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatService {


    private final ChatRepository chatRepository;
    private final AuctionRepository auctionRepository;
    private final MemberRepository memberRepository;

    //웹소켓으로 받은 채팅내역 저장
    public void saveMessage(ChatRequestDto dto) {
        // 1. 채팅 메시지를 DB에 저장
        //해당 경매 찾기
        Auction foundAuction = auctionRepository.findById(dto.auctionId()).orElseThrow(() -> new RuntimeException("Auction not found"));
        Optional<Member> foundMember = memberRepository.findById(dto.memberId());
        Member member = foundMember.get();

        //채팅 내역 저장
        Chat chatEntity = Chat.builder()
                .member(member)
                .auction(foundAuction)
                .message(dto.message())
                .sentAt(dto.sentAt())
                .build();
        chatRepository.save(chatEntity);
    }

    //채팅 내역 조회 - 채팅 시간 순
    public List<ChatResponseDto> getChat(Long auctionId)
    {
        List<Chat> foundChat = chatRepository.findByAuctionIdOrderBySentAtAsc(auctionId);
        List<ChatResponseDto> getChat = foundChat.stream().map(chat -> ChatResponseDto.fromEntity(chat)).collect(Collectors.toList());
        return getChat;
    }
}
