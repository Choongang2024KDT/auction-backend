package com.choongang.auction.streamingauction.repository;

import com.choongang.auction.streamingauction.domain.auctionboard.entity.AuctionBoard;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionBoardRepository extends JpaRepository<AuctionBoard, Long> {
    // 상품 ID 목록으로 경매 게시판 조회
    List<AuctionBoard> findByProductProductIdIn(List<Long> productIds);

    // 특정 상품의 경매 게시판 조회
    AuctionBoard findByProduct(Product product);

    // 상품으로 경매 게시판 삭제
    void deleteByProduct(Product product);
}