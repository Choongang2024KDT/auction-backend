package com.choongang.auction.streamingauction.repository;

import com.choongang.auction.streamingauction.domain.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction,Long> {


}
