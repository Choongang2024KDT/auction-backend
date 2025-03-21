package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.requestDto.BidRequestDto;
import com.choongang.auction.streamingauction.domain.dto.responseDto.BidResponseDto;
import com.choongang.auction.streamingauction.domain.entity.Auction;
import com.choongang.auction.streamingauction.domain.entity.Bid;
import com.choongang.auction.streamingauction.domain.member.dto.response.MemberDTO;
import com.choongang.auction.streamingauction.domain.member.entity.Member;
import com.choongang.auction.streamingauction.domain.member.mapper.MemberMapper;
import com.choongang.auction.streamingauction.repository.AuctionRepository;
import com.choongang.auction.streamingauction.repository.BidRepository;
import com.choongang.auction.streamingauction.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final AuctionService auctionService;
    private final MemberRepository memberRepository;


    //입찰 저장 후 입찰 내역 전송
    public BidResponseDto saveAndGetMaxBid(BidRequestDto bidRequestDto) {

        Auction foundAuction = auctionRepository.findById(bidRequestDto.auctionId()).orElseThrow(()-> new RuntimeException("Auction not found"));
        Optional<Member> foundMember = memberRepository.findById(bidRequestDto.memberId()); //회원 조회
        Member member = foundMember.get(); //회원 정보
        //경매 현재가 업데이트
        auctionService.updateAuctionCurrentPrice(foundAuction.getId() , bidRequestDto.bidAmount());
        //입찰 저장
        Bid bidEntity = Bid.builder()
                .member(member)
                .auction(foundAuction) //요청받은 id를 이용해 찾아낸 해당 경매를 설정 (fk로 auction_id가 설정되어 있어서 자동으로 입력해줌)
                .bidAmount(bidRequestDto.bidAmount())
                .build();
        bidRepository.save(bidEntity);

        //최고가 입찰내역 가져오기
        Bid highestBid = bidRepository.findTopByAuctionIdOrderByBidAmountDesc(bidRequestDto.auctionId());
        if (highestBid == null) {
            return null;  // 최고가 입찰이 없다면 null 반환
        }

        // 즉시입찰가보다 높은 금액일 때
        if (highestBid.getBidAmount() >= foundAuction.getProduct().getBuyNowPrice()){
            log.info("입찰가가 즉시 입찰가를 넘어서 경매가 종료됩니다.");
        }

        // BidResponseDto record를 사용하여 생성
        return new BidResponseDto(
                highestBid.getMember().getName(),  // 최고 입찰자
                highestBid.getBidAmount()           // 입찰 금액
        );
    }

    //입찰가 저장
    public void saveBid(BidRequestDto dto) {
        Auction foundAuction = auctionRepository.findById(dto.auctionId()).orElseThrow(()-> new RuntimeException("Auction not found"));
        Optional<Member> foundMember = memberRepository.findById(dto.memberId()); //회원 조회
        Member member = foundMember.get(); //회원 정보

        Bid bidEntity = Bid.builder()
                .member(member)
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
                highestBid.getMember().getUsername(),  // 사용자 ID
                highestBid.getBidAmount()           // 입찰 금액
        );
    }
}
