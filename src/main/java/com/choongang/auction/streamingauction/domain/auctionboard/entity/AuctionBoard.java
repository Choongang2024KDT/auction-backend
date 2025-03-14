package com.choongang.auction.streamingauction.domain.auctionboard.entity;

import com.choongang.auction.streamingauction.domain.category.entity.Category;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_auctionboard")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "name")
    private String name;

    @Column(name = "content")
    private String content;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_name")
    private Category categoryName;

    @Column(name = "start_price", precision = 10, scale = 2)
    private BigDecimal startPrice;

    @Column(name = "bid_increase", precision = 10, scale = 2)
    private BigDecimal bidIncrease;

    @Column(name = "buy_now_price", precision = 10, scale = 2)
    private BigDecimal buyNowPrice;

    @Column(name = "image_url")
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}