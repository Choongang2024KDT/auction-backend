package com.choongang.auction.streamingauction.domain.product.domain.entity;

import com.choongang.auction.streamingauction.domain.category.entity.Category;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@ToString(exclude = "images") // 순환 참조 방지
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "images") // 순환 참조 방지
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;


    @Builder.Default // Builder에서도 기본값 사용
    @JsonManagedReference // JSON 직렬화 설정
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
}