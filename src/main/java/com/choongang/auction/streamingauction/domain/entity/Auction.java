package com.choongang.auction.streamingauction.domain.entity;

import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = {"product"})
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
    private Long id;

    @OneToOne  // 각 상품은 하나의 경매 방에만 속하고, 각 경매 방은 하나의 상품에만 해당합니다.
    @JoinColumn(name = "product_id")
    private Product product;  // 경매가 연결된 상품

    @CreationTimestamp //시작 시간
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time") //경매 종료 시간 (status가 COMPELTED될때 업데이트)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)  // Enum을 문자열로 저장
    private Status status ; //초기값 ONGOING

    @Column(name = "current_price") // 현재가
    private Long currentPrice;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = Status.ONGOING;  // status가 null인 경우 기본값 설정
        }
    }

}
