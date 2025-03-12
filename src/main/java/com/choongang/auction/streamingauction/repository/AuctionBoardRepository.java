package com.choongang.auction.streamingauction.repository;

import com.choongang.auction.streamingauction.domain.auctionboard.entity.AuctionBoard;

import org.springframework.data.jpa.repository.JpaRepository;


public interface AuctionBoardRepository extends JpaRepository<AuctionBoard, Long> {


}