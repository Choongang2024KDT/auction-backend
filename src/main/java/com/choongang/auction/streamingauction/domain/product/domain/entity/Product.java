package com.choongang.auction.streamingauction.domain.product.domain.entity;

import com.choongang.auction.streamingauction.domain.category.entity.Category;
import com.choongang.auction.streamingauction.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@ToString(exclude = {"images", "member","category"}) // 순환 참조 방지
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"images", "member","category"}) // 순환 참조 방지
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String name;

    @Column(name = "product_description")
    private String description;

    @Column(name = "starting_price")
    private Long startingPrice;

    @Column(name = "bid_increase")
    private Long bidIncrease;

    @Column(name = "buy_now_price")
    private Long buyNowPrice;

    // 카테고리 이름을 저장하는 필드 추가
    @Column(name = "category_name")
    private String categoryName;

    // Category 관계 유지
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    // Member 관계 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default // Builder에서도 기본값 사용
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "product_created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "product_updated_at")
    private LocalDateTime updatedAt;

    // 이미지 추가 헬퍼 메소드
    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    // 대표 이미지 URL 반환 메소드
    public String getMainImageUrl() {
        return images.isEmpty() ? null : images.get(0).getImageUrl();
    }

    // 카테고리 설정 시 카테고리명도 함께 설정하는 메서드
    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            this.categoryName = category.getCategoryType().name();
        }
    }
}