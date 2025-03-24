package com.choongang.auction.streamingauction.domain.participant.entity;

import com.choongang.auction.streamingauction.domain.member.entity.Member;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"product", "member"}) // 순환 참조 방지
@EqualsAndHashCode(exclude = {"product", "member"}) // 순환 참조 방지
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 참가자 상태 (예약됨, 참여중, 취소됨 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ParticipantStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 참가자 상태를 관리하는 Enum
    public enum ParticipantStatus {
        RESERVED, // 예약됨
        PARTICIPATING, // 참여중
        CANCELED // 취소됨
    }

    // 예약 상태로 초기화하는 편의 메서드
    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = ParticipantStatus.RESERVED;
        }
    }
}