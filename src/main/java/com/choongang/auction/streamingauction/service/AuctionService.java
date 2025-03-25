package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.requestDto.AuctionRequestDto;
import com.choongang.auction.streamingauction.domain.dto.responseDto.AuctionResponseDto;
import com.choongang.auction.streamingauction.domain.entity.Auction;
import com.choongang.auction.streamingauction.domain.entity.Bid;
import com.choongang.auction.streamingauction.domain.entity.Status;
import com.choongang.auction.streamingauction.domain.entity.TradeRecord;
import com.choongang.auction.streamingauction.domain.product.domain.dto.ProductDTO;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import com.choongang.auction.streamingauction.domain.product.mapper.ProductMapper;
import com.choongang.auction.streamingauction.exception.AuctionNotFoundException;
import com.choongang.auction.streamingauction.exception.ForbiddenOperationException;
import com.choongang.auction.streamingauction.repository.AuctionRepository;
import com.choongang.auction.streamingauction.repository.BidRepository;
import com.choongang.auction.streamingauction.repository.ProductRepository;
import com.choongang.auction.streamingauction.repository.TradeRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final BidRepository bidRepository;
    private final TradeRecordRepository tradeRecordRepository;
    private final NotificationService notificationService;

    //경매 생성
    public AuctionResponseDto createAuction(AuctionRequestDto auctionRequestDto) {

        Optional<Product> getProductInfo = productRepository.findByIdWithImages(auctionRequestDto.productId());

        // 해당 상품 id의 게시글이 없을 경우, isEmpty()로 체크
        if (getProductInfo.isEmpty()) {
            return AuctionResponseDto.builder()
                    .success(false)
                    .message("해당 상품은 등록되지 않은 상품입니다.")
                    .build();
        }
        // 조회된 상품
        Product foundProduct = getProductInfo.get();

        //Auction 객체 생성
        Auction auctionEntity = Auction.builder()
                .product(foundProduct)
                .build();


        // 이제 양방향 관계 설정을 위해 헬퍼 메소드 사용
        auctionEntity.setProduct(foundProduct);

        // 경매 저장
        Auction savedAuction = auctionRepository.save(auctionEntity);

        // 생성된 경매에서 Product 엔티티를 ProductDtO로 변환
        ProductDTO productDTO = productMapper.toDto(savedAuction.getProduct());

        // 저장된 경매 정보와 함께 성공 응답 반환
        return AuctionResponseDto.builder()
                .id(savedAuction.getId())  // 생성된 경매의 ID 반환
                .product(productDTO)
                .currentPrice(savedAuction.getCurrentPrice())
                .startTime(savedAuction.getStartTime())
                .endTime(savedAuction.getEndTime())
                .status(savedAuction.getStatus())
                .success(true)
                .message("경매가 성공적으로 생성되었습니다.")
                .build();
    }

    //경매 정보 요청
    public AuctionResponseDto getAuctionInfo(Long productId) {
        Optional<Auction> foundAuctionByProductId = auctionRepository.findByProduct_ProductId(productId);

        // 경매 정보가 없을 경우, isEmpty()로 체크
        if (foundAuctionByProductId.isEmpty()) {
            return AuctionResponseDto.builder()
                    .success(false)
                    .message("경매 정보가 없습니다.")
                    .build();
        }

        Auction foundAuction = foundAuctionByProductId.get();

        // Product 엔티티를 ProductDtO로 변환
        ProductDTO productDTO = productMapper.toDto(foundAuction.getProduct());

        return AuctionResponseDto.builder()
                .id(foundAuction.getId())
                .currentPrice(foundAuction.getCurrentPrice())
                .product(productDTO)  // Entity 대신 DTO 사용

                .startTime(LocalDateTime.now())
                .status(foundAuction.getStatus())
                .success(true)  // 성공 상태
                .message("경매 정보가 정상적으로 반환되었습니다.")  // 정상 메시지
                .build();
    }

    //경매 현재 진행가 업데이트
    public void updateAuctionCurrentPrice(Long id , Long bidAmount) {
        //업데이트할 경매를 조회
        Optional<Auction> foundAuction = auctionRepository.findById(id);
        Auction auctionEntity = foundAuction.get();
        auctionEntity.setCurrentPrice(bidAmount); //현재가만 업데이트

        auctionRepository.save(auctionEntity); // 현재가 업데이트
    }

    // 판매자가 경매 종료 처리 (auction status 'COMPLETED' 로 업데이트 + 종료시간 업데이트)
    public void closeAuctionBySeller(AuctionRequestDto auctionRequestDto , Long memberId) {
        // 해당 상품의 경매방 찾기
        Optional<Auction> foundAuctionByProductId = auctionRepository.findByProduct_ProductId(auctionRequestDto.productId());

        // 경매 정보가 없을 경우, 로깅 후 종료
        foundAuctionByProductId.ifPresentOrElse(
                auctionEntity -> {
                    // 경매 상태가 이미 '완료'인 경우 처리 (중복 종료 방지)
                    if (auctionEntity.getStatus() == Status.COMPLETED) {
                        log.info("이미 종료된 경매입니다. 상품 ID: {}", auctionRequestDto.productId());
                        return;
                    }
                    //판매자가 아닌 사용자가 경매를 종료요청하면 에러처리
                    if (!auctionEntity.getProduct().getMember().getId().equals(memberId)) {
                        throw new ForbiddenOperationException("판매자만 경매를 종료할 수 있습니다.");
                    }

                    // 최고 입찰자 조회 - 오버로딩이 안 된 상태에선 불가피 한듯..
                    Bid highestBid = null;
                    try {
                        highestBid = bidRepository.findTopByAuctionIdOrderByBidAmountDesc(auctionEntity.getId());
                    } catch (Exception e) {
                        log.error("최고 입찰 조회 중 오류 발생. 경매 ID: {}, 오류: {}", auctionEntity.getId(), e.getMessage());
                    }

                    // 종료 시간 업데이트, 경매 상태 변경
                    auctionEntity.setEndTime(LocalDateTime.now());
                    auctionEntity.setStatus(Status.COMPLETED);

                    // 경매 종료 처리 후 DB에 저장
                    auctionRepository.save(auctionEntity);

                    // 경매 종료 로그 추가
                    log.info("경매 종료됨. 상품 ID: {}, 종료 시간: {}", auctionRequestDto.productId(), auctionEntity.getEndTime());

                    // 최고 입찰자 조회 - 오버로딩이 안 된 상태에선 불가피 한듯..
                    Long sellerId = auctionEntity.getProduct().getMember().getId();

                    if (highestBid != null) {
                        Long winnerId = highestBid.getMember().getId();

                        TradeRecord tradeRecord = TradeRecord.builder()
                                .itemName(auctionEntity.getProduct().getName())
                                .amount(auctionEntity.getCurrentPrice())
                                .seller(sellerId)
                                .buyer(winnerId)
                                .productId(auctionRequestDto.productId())
                                .build();

                        tradeRecordRepository.save(tradeRecord);
                        log.info("TradeRecord 저장 완료. Trade ID: {}", tradeRecord.getTradeId());

                        notificationService.handleAuctionEnd(auctionRequestDto.productId(), sellerId, winnerId);
                    } else {
                        log.info("종료된 경매에 입찰 내역이 없습니다 또는 조회 실패. 상품 ID: {}", auctionRequestDto.productId());
                    }
                },
                () -> {
                    throw new AuctionNotFoundException("경매가 존재하지 않습니다.");
                }
        );
    }

    // 입찰자의 입찰에 의한 경매 종료 처리 (auction status 'COMPLETED'로 업데이트 + 종료시간 업데이트)
    public void closeAuctionByBuyer(AuctionRequestDto auctionRequestDto) {
        // 해당 상품의 경매방 찾기
        Optional<Auction> foundAuctionByProductId = auctionRepository.findByProduct_ProductId(auctionRequestDto.productId());

        // 경매 정보가 없을 경우, 로깅 후 종료
        foundAuctionByProductId.ifPresentOrElse(
                auctionEntity -> {
                    // 경매 상태가 이미 '완료'인 경우 처리 (중복 종료 방지)
                    if (auctionEntity.getStatus() == Status.COMPLETED) {
                        log.info("이미 종료된 경매입니다. 상품 ID: {}", auctionRequestDto.productId());
                        return;
                    }

                    // 종료 시간 업데이트, 경매 상태 변경
                    auctionEntity.setEndTime(LocalDateTime.now());
                    auctionEntity.setStatus(Status.COMPLETED);

                    // 경매 종료 처리 후 DB에 저장
                    auctionRepository.save(auctionEntity);

                    // 경매 종료 로그 추가
                    log.info("경매 종료됨. 상품 ID: {}, 종료 시간: {}", auctionRequestDto.productId(), auctionEntity.getEndTime());

                    // 최고 입찰자 조회 - 입찰 로직에 있지만 경매종료 2가지 조건에 따라 오버로딩으로 나누지 않는 상황이라면
                    // 일단 넣어두기
                    Bid highestBid = bidRepository.findTopByAuctionIdOrderByBidAmountDesc(auctionEntity.getId());
                    if (highestBid == null) {
                        log.info("종료된 경매에 입찰 내역이 없습니다. 상품 ID: {}", auctionRequestDto.productId());
                    } else {
                        // TradeRecord 생성 및 저장
                        TradeRecord tradeRecord = TradeRecord.builder()
                                .itemName(auctionEntity.getProduct().getName()) // 상품 이름
                                .amount(auctionEntity.getCurrentPrice())        // 낙찰 금액 (Auction의 현재가)
                                .seller(auctionEntity.getProduct().getMember().getId())            // 판매자 ID
                                .buyer(highestBid.getMember().getId())          // 낙찰자 ID (Bid에서)
                                .productId(auctionRequestDto.productId())    // 상품 ID
                                .build();

                        tradeRecordRepository.save(tradeRecord);
                        log.info("TradeRecord 저장 완료. Trade ID: {}", tradeRecord.getTradeId());
                    }
                    // 후속 처리: 낙찰자에게 알림, 판매 상태로 변경 등 추가적인 로직 처리
                    // 예: notifyWinner(auctionEntity.getWinningUserName());
                },
                () -> log.info("경매가 존재하지 않습니다. 상품 ID: {}", auctionRequestDto.productId()) // 경매가 없을 경우 로깅
        );
    }


}
