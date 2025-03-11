package com.choongang.auction.streamingauction.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder

@Entity
@Table(name = "Bid")  // 실제 데이터베이스 테이블명
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_id")
    private Long bidId;

    @Column(name = "user_id", nullable = false)  // 현재 입찰한 구매자의 id 추후에 User테이블과 매핑 필요
    private String userId;

    @Column(name = "auction_id")  // 어떤 경매방에서 진행된 입찰인지 구분 추후에 AuctionStream테이블과 매핑 필요
    private String auctionId;

    @Column(name = "bid_amount")  // 입찰가
    private Long bidAmount;

    @CreationTimestamp
    @Column(name = "bid_time", updatable = false)  // 입찰시간
    private LocalDateTime bidTime;
}
