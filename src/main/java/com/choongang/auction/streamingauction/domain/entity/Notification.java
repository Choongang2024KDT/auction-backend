package com.choongang.auction.streamingauction.domain.entity;

import com.choongang.auction.streamingauction.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "notifications")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column
    private String link;

    @Column(nullable = false)
    private boolean isRead;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", updatable = false)
    private LocalDateTime createdAt;

    private String safeNumber;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime expiresAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
