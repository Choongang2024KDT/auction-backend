package com.choongang.auction.streamingauction.repository;

import com.choongang.auction.streamingauction.domain.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction,Long> {

    // Auction 엔티티에서 productId로 조회
    // 수정된 메소드 이름
    Optional<Auction> findByProduct_ProductId(Long productId);
}
