package com.choongang.auction.streamingauction.repository;

import com.choongang.auction.streamingauction.domain.entity.Bid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface BidRepository extends JpaRepository<Bid,Long> {


    Bid findTopByAuctionIdOrderByBidAmountDesc(Long auctionId); //입찰가로 정렬 후 상위 정보 조회 or null값 반환


}
