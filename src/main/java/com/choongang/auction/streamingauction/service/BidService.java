package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.requestDto.BidRequestDto;
import com.choongang.auction.streamingauction.domain.entity.Bid;
import com.choongang.auction.streamingauction.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BidService {

    private final BidRepository bidRepository;

    //입찰가 저장
    public void saveBid(BidRequestDto dto) {
        Bid bidEntity = Bid.builder()
                .userId(dto.userId())
                .auctionId(dto.auctionId())
                .bidAmount(dto.bidAmount())
                .build();
        bidRepository.save(bidEntity);
    }

    //최고 입찰가 조회
    //최고 입찰가 null상태일때 예외처리 후 controller로 반환
    public Bid getMaxBid() {
        return bidRepository.findTopByOrderByBidAmountDesc()
                .orElseThrow(()->new NoSuchElementException("No bids found"));
    }
}
