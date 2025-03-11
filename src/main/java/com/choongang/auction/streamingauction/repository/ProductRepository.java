package com.choongang.auction.streamingauction.repository;

import com.choongang.auction.streamingauction.product.domain.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 이미지를 함께 로드하는 쿼리
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.productId = :id")
    Optional<Product> findByIdWithImages(@Param("id") Long id);
}