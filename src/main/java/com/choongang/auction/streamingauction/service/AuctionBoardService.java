package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.auctionboard.entity.AuctionBoard;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import com.choongang.auction.streamingauction.repository.AuctionBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuctionBoardService {

    private final AuctionBoardRepository auctionBoardRepository;

    // 모든 게시물 조회
    @Transactional(readOnly = true)
    public List<AuctionBoard> findAllAuctionBoards() {
        return auctionBoardRepository.findAll();
    }

    // ID로 게시물 조회
    @Transactional(readOnly = true)
    public AuctionBoard findAuctionBoardById(Long boardId) {
        return auctionBoardRepository.findById(boardId).orElseThrow(() ->
                new RuntimeException("조회된 게시물이 없습니다. ID: " + boardId));
    }

    // 상품 ID 목록으로 경매 게시판 조회
    @Transactional(readOnly = true)
    public List<AuctionBoard> findAuctionBoardsByProductIds(List<Long> productIds) {
        log.info("상품 ID 목록으로 경매 게시판 조회: {}", productIds);
        return auctionBoardRepository.findByProductProductIdIn(productIds);
    }

    // 특정 상품의 경매 게시판 조회
    @Transactional(readOnly = true)
    public AuctionBoard findAuctionBoardByProduct(Product product) {
        log.info("상품으로 경매 게시판 조회: ID={}", product.getProductId());
        return auctionBoardRepository.findByProduct(product);
    }

    // 게시물 삭제
    public void deleteByAuctionBoard(Long boardId) {
        log.info("경매 게시물 삭제: ID={}", boardId);

        // 게시물이 존재하는지 확인
        AuctionBoard auctionBoard = auctionBoardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("삭제할 게시물이 존재하지 않습니다. ID: " + boardId));

        // 게시물 삭제
        auctionBoardRepository.deleteById(boardId);

        log.info("경매 게시물 삭제 완료: ID={}", boardId);
    }

    // 상품으로 게시물 삭제
    public void deleteByProduct(Product product) {
        log.info("상품의 경매 게시물 삭제: 상품 ID={}", product.getProductId());
        auctionBoardRepository.deleteByProduct(product);
        log.info("상품의 경매 게시물 삭제 완료: 상품 ID={}", product.getProductId());
    }

    // 회원이 등록한 경매 게시판 조회
    @Transactional(readOnly = true)
    public List<AuctionBoard> findAuctionBoardsByMember(String username, List<Product> products) {
        log.info("회원({})이 등록한 상품의 경매 게시판 조회", username);

        // 상품 ID 목록 추출
        List<Long> productIds = products.stream()
                .map(Product::getProductId)
                .toList();

        return findAuctionBoardsByProductIds(productIds);
    }
}