package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.requestDto.AuctionRequestDto;
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

    // 입찰 저장 후 입찰 내역 전송
    public BidResponseDto saveAndGetMaxBid(BidRequestDto bidRequestDto) {

        Auction foundAuction = auctionRepository.findById(bidRequestDto.auctionId()).orElseThrow(()-> new RuntimeException("Auction not found"));
        Optional<Member> foundMember = memberRepository.findById(bidRequestDto.memberId()); //회원 조회
        if (foundMember.isEmpty()) {
            throw new RuntimeException("Member not found"); // 멤버가 없으면 예외 처리
        }
        Member member = foundMember.get(); //회원 정보

        // 입찰 entity 생성
        Bid bidEntity = Bid.builder()
                .member(member)
                .auction(foundAuction) //요청받은 id를 이용해 찾아낸 해당 경매를 설정 (fk로 auction_id가 설정되어 있어서 자동으로 입력해줌)
                .bidAmount(bidRequestDto.bidAmount())
                .build();
        //현재 최고가 조회
        Bid highestBid = bidRepository.findTopByAuctionIdOrderByBidAmountDesc(bidRequestDto.auctionId());
        // 첫 입찰 시에는 최고가 조회시 null이기떄문에 바로 입찰 처리
        if (highestBid == null) {
            log.info("No current bid found.");
            //저장
            bidRepository.save(bidEntity);
            //경매 현재가 업데이트
            auctionService.updateAuctionCurrentPrice(foundAuction.getId() , bidRequestDto.bidAmount());
            return new BidResponseDto(
                    bidEntity.getMember().getName(),  // 최고 입찰자
                    bidEntity.getBidAmount()           // 입찰 금액
            );
        }

        // 즉시입찰가보다 높은 금액이 조회됐을 땐 경매 종료 + 현재 입찰 저장 x
        if (highestBid.getBidAmount() >= foundAuction.getProduct().getBuyNowPrice()){
            log.info("이미 즉시 낙찰가가 있습니다.");
            //경매 종료
            auctionService.closeAuctionBySeller(new AuctionRequestDto(foundAuction.getProduct().getProductId()));
            // 조회된 최고 입찰자의 정보 반환
            return new BidResponseDto(
                    highestBid.getMember() != null ? highestBid.getMember().getName() : "Unknown",
                    highestBid.getBidAmount()
            );
        } else if (bidEntity.getBidAmount() >= foundAuction.getProduct().getBuyNowPrice()) {
        //즉시 낙찰 기록은 없지만 현재 입찰가가 즉시 낙찰이 가능한 경우
            log.info("즉시 낙찰가보다 높은 금액으로 낙찰됐습니다.");
            bidRepository.save(bidEntity);
            //경매 현재가 업데이트
            auctionService.updateAuctionCurrentPrice(foundAuction.getId() , bidRequestDto.bidAmount());
            //경매 종료
            auctionService.closeAuctionBySeller(new AuctionRequestDto(foundAuction.getProduct().getProductId()));
            return new BidResponseDto(
                    bidEntity.getMember().getName(),  // 최고 입찰자
                    bidEntity.getBidAmount()           // 입찰 금액
            );
        } else {
        // 조회 시 현재 즉시입찰이 안된 경우 입찰가 저장 + 경매 정보 업데이트 + 반환
            bidRepository.save(bidEntity);
            //경매 현재가 업데이트
            auctionService.updateAuctionCurrentPrice(foundAuction.getId() , bidRequestDto.bidAmount());
            return new BidResponseDto(
                    bidEntity.getMember().getName(),  // 최고 입찰자
                    bidEntity.getBidAmount()           // 입찰 금액
            );
        }
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
