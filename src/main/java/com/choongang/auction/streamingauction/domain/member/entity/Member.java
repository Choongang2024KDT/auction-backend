package com.choongang.auction.streamingauction.domain.member.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_email", columnList = "email"),
                @Index(name = "idx_username", columnList = "username")
        }
)
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Setter
    @Column(length = 100)
    private String password;

    @Column(unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;


    @Column(nullable = false, length = 20)
    private String role = "ROLE_USER";

    @Column(length = 255)
    private String refreshToken;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;


    @Builder
    private Member(String username, String password, String email,
                    String name) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
    }
}
