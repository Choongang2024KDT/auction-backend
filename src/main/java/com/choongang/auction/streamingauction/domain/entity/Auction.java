package com.choongang.auction.streamingauction.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder

@Table(name = "Auction")
@Entity
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auction_id")
    private Long auctionId;

    @Column(name = "product_id") //FK product 테이블
    private Long productId;

    @Column(name = "user_id") //경매 주최자(판매자) FK user 테이블
    private Long userId;

    @Column(name = "title") //경매 제목
    private String title;

    @Column(name = "description") //경매 설명 ex)상품 설명
    private String description;

    @CreationTimestamp //시작 시간
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time") //경매 종료 시간 (status가 COMPELTED될때 업데이트)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)  // Enum을 문자열로 저장
    private Status status ; //초기값 ONGOING

    @Column(name = "starting_price") //경매 시작가
    private Long startingPrice;

    @Column(name = "current_price") // 현재가
    private Long current_price;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = Status.ONGOING;  // status가 null인 경우 기본값 설정
        }
    }
}
