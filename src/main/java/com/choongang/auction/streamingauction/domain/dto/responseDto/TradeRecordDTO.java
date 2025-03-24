package com.choongang.auction.streamingauction.domain.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeRecordDTO {
    private Long tradeId;
    private String itemName;
    private Long amount;
    private Long productId; // 추후에 해당 상품 페이지로 이동 용도
    private Timestamp createdAt;
    private String opponentName;  // 상대방 이름 (판매자/구매자에 따라 다름)
}