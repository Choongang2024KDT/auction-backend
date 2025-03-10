package com.choongang.auction.streamingauction.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder

@Entity
@Table(name = "product")  // 실제 데이터베이스 테이블명
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")  // 컬럼명: product_id (PK)
    private Long productId;

    @Column(name = "product_name", nullable = false)  // 컬럼명: product_name
    private String name;

    @Column(name = "product_description")  // 컬럼명: product_description
    private String description;

    @Column(name = "product_category")  // 컬럼명: product_category
    private String category;

    @Column(name = "product_image_url")  // 컬럼명: product_image_url
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "product_created_at", updatable = false)  // 컬럼명: product_created_at
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "product_updated_at")  // 컬럼명: product_updated_at
    private LocalDateTime updatedAt;

    // ManyToOne 관계로 판매자와 연관
//    @ManyToOne
//    @JoinColumn(name = "product_user_id")  // 외래키: product_user_id (판매자)
//    private User user;
//
//    @OneToOne(mappedBy = "product")
//    private Auction auction;


}