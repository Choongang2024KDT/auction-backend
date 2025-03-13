package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.requestDto.AuctionRequestDto;
import com.choongang.auction.streamingauction.domain.entity.Auction;
import com.choongang.auction.streamingauction.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuctionService {

    private final AuctionRepository auctionRepository;

    //경매 데이터 저장
    public void createAuction(AuctionRequestDto auctionRequestDto) {

        //Auction 객체 생성
        Auction auctionEntity = Auction.builder()
                .productId(auctionRequestDto.productId())
                .userId(auctionRequestDto.userId())
                .title(auctionRequestDto.title())
                .description(auctionRequestDto.description())
                .startingPrice(auctionRequestDto.startingPrice())
                .build();

        //Chat객체 생성

        auctionRepository.save(auctionEntity);
    }

    //경매 정보 요청
    public Optional<Auction> getAuctionInfo(Long auctionId) {
        return auctionRepository.findById(auctionId);
    }
}
