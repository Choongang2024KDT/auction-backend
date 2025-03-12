package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.requestDto.BidRequestDto;
import com.choongang.auction.streamingauction.domain.dto.responseDto.BidResponseDto;
import com.choongang.auction.streamingauction.domain.entity.Auction;
import com.choongang.auction.streamingauction.domain.entity.Bid;
import com.choongang.auction.streamingauction.repository.AuctionRepository;
import com.choongang.auction.streamingauction.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BidService {

    private final SimpMessagingTemplate messagingTemplate;
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;

    //입찰 저장 후 입찰내역 전송
    public void saveAndGetMaxBid(BidRequestDto bidRequestDto) {
        //입찰 저장
        Auction foundAuction = auctionRepository.findById(bidRequestDto.auctionId()).orElseThrow(()-> new RuntimeException("Auction not found"));

        Bid bidEntity = Bid.builder()
                .userId(bidRequestDto.userId())
                .auction(foundAuction) //요청받은 id를 이용해 찾아낸 해당 경매를 설정 (fk로 auction_id가 설정되어 있어서 자동으로 입력해줌)
                .bidAmount(bidRequestDto.bidAmount())
                .build();
        bidRepository.save(bidEntity);

        //최고가 입찰내역 가져오기
        Bid highestBid = bidRepository.findTopByAuctionIdOrderByBidAmountDesc(bidRequestDto.auctionId());

        // 실시간으로 웹소켓을 통해 다른 클라이언트들에게 최고 입찰가 전송
        messagingTemplate.convertAndSend("/topic/bids/" + bidRequestDto.auctionId(), highestBid);

    }

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
    public BidResponseDto getMaxBid(Long auctionId) {

        Bid highestBid = bidRepository.findTopByAuctionIdOrderByBidAmountDesc(auctionId);

        if (highestBid == null) {
            return null;  // 최고가 입찰이 없다면 null 반환
        }
        // BidResponseDto record를 사용하여 생성
        return new BidResponseDto(
                highestBid.getUserId(),  // 사용자 ID
                highestBid.getBidAmount()           // 입찰 금액
        );
    }
}
