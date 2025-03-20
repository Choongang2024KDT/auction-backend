package com.choongang.auction.streamingauction.domain.entity;

import com.choongang.auction.streamingauction.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Getter
@ToString(exclude = {"auction" , "member"})
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "Bid" )  // 실제 데이터베이스 테이블명
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")  // 회원을 참조하는 외래 키
    private Member member;  // 입찰을 진행한 회원

    @ManyToOne(fetch = FetchType.LAZY)// 하나의 경매는 여러 입찰을 가질 수 있다. - 단방향매핑 (상대의 pk를 fk로 갖는 형태)
    @JoinColumn(name = "auction_id") //fk컬럼명
    private Auction auction;

    @Column(name = "bid_amount")  // 입찰가
    private Long bidAmount;

    @CreationTimestamp
    @Column(name = "bid_time", updatable = false)  // 입찰시간
    private LocalDateTime bidTime;
}
