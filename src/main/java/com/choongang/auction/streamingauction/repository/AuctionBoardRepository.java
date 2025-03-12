package com.choongang.auction.streamingauction.repository;

import com.choongang.auction.streamingauction.domain.auctionboard.entity.AuctionBoard;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuctionBoardRepository extends JpaRepository<AuctionBoard, Long> {

    Optional<AuctionBoard> findByProduct(Product product);

    @Query("SELECT ab FROM AuctionBoard ab JOIN FETCH ab.product WHERE ab.boardId = :id")
    Optional<AuctionBoard> findByIdWithProduct(@Param("id") Long id);
}