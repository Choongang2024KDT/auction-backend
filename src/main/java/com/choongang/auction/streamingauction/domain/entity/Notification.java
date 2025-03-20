package com.choongang.auction.streamingauction.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private boolean isRead;

    @CreationTimestamp
    private Timestamp createdAt;

    public Notification(Long userId, String message, String link) {
        this.userId = userId;
        this.message = message;
        this.link = link;
        this.createdAt = null;
    }
}
