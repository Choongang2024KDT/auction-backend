package com.choongang.auction.streamingauction.domain.entity;

import com.choongang.auction.streamingauction.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Getter
@Table(name = "trade_record")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id")
    private Long tradeId;

    // 상품 이름
    @Column(name = "item_name", nullable = false)
    private String itemName;

    // 가격
    @Column(name = "amount", nullable = false)
    private Long amount;

    // 판매자 id
    @Column(name = "seller_id", nullable = false)
    private Long seller;

    // 구매자 id
    @Column(name = "bidder_id", nullable = false)
    private Long buyer;

    // 상품 번호 (추후 해당 상품 사전 등록 페이지로 이동 용도)
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // 생성 시간
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;
}