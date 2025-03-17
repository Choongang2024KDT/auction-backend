package com.choongang.auction.streamingauction.repository;

import com.choongang.auction.streamingauction.domain.category.entity.Category;
import com.choongang.auction.streamingauction.domain.member.entity.Member;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 이미지를 함께 로드하는 쿼리
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.productId = :id")
    Optional<Product> findByIdWithImages(@Param("id") Long id);

    // 카테고리별 상품 조회
    List<Product> findByCategory(Category category);

    // 회원별 상품 조회
    List<Product> findByMember(Member member);

    // 회원 이름으로 상품 조회
    List<Product> findByMemberUsername(String username);

    // 상품명으로 검색 (부분 일치)
    List<Product> findByNameContaining(String keyword);
}