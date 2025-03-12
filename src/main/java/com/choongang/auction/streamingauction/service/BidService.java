package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.requestDto.BidRequestDto;
import com.choongang.auction.streamingauction.domain.entity.Auction;
import com.choongang.auction.streamingauction.domain.entity.Bid;
import com.choongang.auction.streamingauction.repository.AuctionRepository;
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
    private final AuctionRepository auctionRepository;
    //입찰가 저장
    public void saveBid(BidRequestDto dto) {
        Auction foundAuction = auctionRepository.findById(dto.auctionId()).orElseThrow(()-> new RuntimeException("Auction not found"));

        Bid bidEntity = Bid.builder()
                .userId(dto.userId())
                .auction(foundAuction) //요청받은 id를 이용해 찾아낸 해당 경매를 설정 (fk로 auction_id가 설정되어 있어서 자동으로 입력해줌)
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
