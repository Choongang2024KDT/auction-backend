package com.choongang.auction.streamingauction.repository;

import com.choongang.auction.streamingauction.domain.entity.TradeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRecordRepository extends JpaRepository<TradeRecord, Long> {
}
