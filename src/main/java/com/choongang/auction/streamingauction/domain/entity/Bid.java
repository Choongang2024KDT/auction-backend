package com.choongang.auction.streamingauction.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Getter
@ToString(exclude = {"auction"})
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
    private Long id;

    @Column(name = "user_id", nullable = false)  // 현재 입찰한 구매자의 id 추후에 User테이블과 매핑 필요
    private Long userId;

//    @Column(name = "auction_id")  // 어떤 경매방에서 진행된 입찰인지 구분 추후에 AuctionStream테이블과 매핑 필요
//    private Long auctionId;
    @ManyToOne(fetch = FetchType.LAZY)//하나의 경매는 여러 입찰을 가질 수 있다. - 단방향매핑 (상대의 pk를 fk로 갖는 형태)
    @JoinColumn(name = "auction_id") //fk컬럼명
    private Auction auction;

    @Column(name = "bid_amount")  // 입찰가
    private Long bidAmount;

    @CreationTimestamp
    @Column(name = "bid_time", updatable = false)  // 입찰시간
    private LocalDateTime bidTime;
}
