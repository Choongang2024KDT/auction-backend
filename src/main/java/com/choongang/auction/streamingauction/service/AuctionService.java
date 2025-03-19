package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.requestDto.AuctionRequestDto;
import com.choongang.auction.streamingauction.domain.dto.responseDto.AuctionResponseDto;
import com.choongang.auction.streamingauction.domain.entity.Auction;
import com.choongang.auction.streamingauction.domain.product.domain.dto.ProductDTO;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import com.choongang.auction.streamingauction.domain.product.mapper.ProductMapper;
import com.choongang.auction.streamingauction.repository.AuctionRepository;
import com.choongang.auction.streamingauction.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    //경매 데이터 저장
    public void createAuction(AuctionRequestDto auctionRequestDto) {

        Optional<Product> getProductInfo = productRepository.findByIdWithImages(auctionRequestDto.productId());
        Product foundProduct = getProductInfo.get();
        //Auction 객체 생성
        Auction auctionEntity = Auction.builder()
                .product(foundProduct)
//                .userId(auctionRequestDto.userId())
//                .title(auctionRequestDto.title())
//                .description(auctionRequestDto.description())
//                .startingPrice(auctionRequestDto.startingPrice())
                .build();

        //Chat객체 생성

        auctionRepository.save(auctionEntity);
    }

    //경매 정보 요청
    public AuctionResponseDto getAuctionInfo(Long productId) {
        Optional<Auction> foundProduct = auctionRepository.findByProduct_ProductId(productId);

        // 경매 정보가 없을 경우, isEmpty()로 체크
        if (foundProduct.isEmpty()) {
            return AuctionResponseDto.builder()
                    .success(false)
                    .message("경매 정보가 없습니다.")
                    .build();
        }

        Auction getProduct = foundProduct.get();

        // Product 엔티티를 ProductDTO로 변환
        ProductDTO productDTO = productMapper.toDto(getProduct.getProduct());

        return AuctionResponseDto.builder()
                .id(getProduct.getId())
//                .userId(getProduct.) //상품을 게시한 유저의 아이디 (판매자)
                .currentPrice(getProduct.getCurrentPrice())
                .product(productDTO)  // Entity 대신 DTO 사용
                .startTime(LocalDateTime.now())
                .status(getProduct.getStatus())
                .success(true)  // 성공 상태
                .message("경매 정보가 정상적으로 반환되었습니다.")  // 정상 메시지
                .build();
    }
    //경매 현재 진행가 업데이트
    public void updateAuctionCurrentPrice(Long id , Long bidAmount) {
        //업데이트할 경매를 조회
        Optional<Auction> foundAuction = auctionRepository.findById(id);
        Auction auctionEntity = foundAuction.get();
        auctionEntity.setCurrentPrice(bidAmount); //currentPrice만 업데이트

        auctionRepository.save(auctionEntity); // 현재가 업데이트
    }
}
