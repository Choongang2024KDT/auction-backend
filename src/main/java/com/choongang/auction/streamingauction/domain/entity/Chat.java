package com.choongang.auction.streamingauction.domain.entity;

import com.choongang.auction.streamingauction.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "chat")
@ToString(exclude = {"auction", "member"})
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;  // User 테이블과 연관된 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")  // 회원을 참조하는 외래 키
    private Member member;  // 입찰을 진행한 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;  // Auction 테이블과 연관된 외래키 //하나의 경매는 여러개의 채팅내역을 가질 수 있다.

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;
}