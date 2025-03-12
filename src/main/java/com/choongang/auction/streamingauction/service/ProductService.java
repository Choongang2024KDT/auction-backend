package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.auctionboard.entity.AuctionBoard;
import com.choongang.auction.streamingauction.domain.category.entity.Category;
import com.choongang.auction.streamingauction.domain.category.entity.CategoryType;
import com.choongang.auction.streamingauction.domain.product.domain.dto.ProductCreate;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import com.choongang.auction.streamingauction.domain.product.domain.entity.ProductImage;
import com.choongang.auction.streamingauction.repository.AuctionBoardRepository;
import com.choongang.auction.streamingauction.repository.CategoryRepository;
import com.choongang.auction.streamingauction.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AuctionBoardRepository auctionBoardRepository;

    /**
     * 상품을 이미지와 함께 저장하고 경매 게시판에 등록합니다.
     *
     * @param dto 상품 생성 DTO
     * @param imageUrls 이미지 URL 목록
     * @return 저장된 상품 엔티티
     */
    public Product saveProductWithImages(ProductCreate dto, List<String> imageUrls) {
        log.info("상품 등록 시작: {}, 이미지 개수: {}", dto.productName(), imageUrls != null ? imageUrls.size() : 0);

        // 카테고리 조회
        Category category = findCategoryByType(dto.productCategory());
        log.info("카테고리 조회 결과: ID={}, Type={}",
                category.getCategoryId(), category.getCategorytype());

        // 상품 엔티티 생성 (가격 필드 없음)
        Product productEntity = Product.builder()
                .name(dto.productName())
                .description(dto.productDescription())
                .category(category)  // 카테고리 설정
                .images(new ArrayList<>())  // 빈 이미지 리스트로 초기화
                .build();

        // 이미지 URL이 있는 경우 ProductImage 엔티티 생성 및 연결
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                ProductImage productImage = ProductImage.builder()
                        .imageUrl(imageUrl)
                        .product(productEntity)
                        .build();

                // Product 엔티티의 images 리스트에 추가
                productEntity.getImages().add(productImage);
            }

            log.info("이미지 {} 개가 상품에 추가되었습니다.", imageUrls.size());
        }

        // 상품 저장
        Product savedProduct = productRepository.save(productEntity);

        // 경매 게시판 등록 (가격 정보는 AuctionBoard에 저장)
        AuctionBoard auctionBoard = AuctionBoard.builder()
                .product(savedProduct)
                .name(dto.productName())
                .content(dto.productDescription())
                .price(BigDecimal.valueOf(dto.productStartPrice()))
                .bidIncrease(BigDecimal.valueOf(dto.productBidIncrement()))
                .buyNowPrice(BigDecimal.valueOf(dto.productBuyNowPrice()))
                .imageUrl(imageUrls != null && !imageUrls.isEmpty() ? imageUrls.get(0) : null)
                .build();

        auctionBoardRepository.save(auctionBoard);

        log.info("저장된 상품: ID={}, 카테고리={}, 이미지 개수={}",
                savedProduct.getProductId(),
                savedProduct.getCategory().getCategorytype(),
                savedProduct.getImages().size());

        return savedProduct;
    }

    /**
     * ID로 상품을 조회합니다.
     *
     * @param id 상품 ID
     * @return 조회된 상품 엔티티
     */
    @Transactional(readOnly = true)
    public Product findProduct(Long id) {
        // 이미지를 함께 조회하는 쿼리 사용
        Product product = productRepository.findByIdWithImages(id)
                .orElseGet(() -> productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다. ID: " + id)));

        log.info("상품 조회: ID={}, 카테고리={}, 이미지 개수={}",
                product.getProductId(),
                product.getCategory().getCategorytype(),
                product.getImages().size());

        return product;
    }

    /**
     * 카테고리 타입 문자열로 카테고리 엔티티를 찾습니다.
     *
     * @param categoryTypeName 카테고리 타입 문자열 (예: "BEAUTY")
     * @return 찾은 카테고리 엔티티
     */
    private Category findCategoryByType(String categoryTypeName) {
        try {
            // 문자열을 CategoryType enum으로 변환
            CategoryType categoryType = CategoryType.valueOf(categoryTypeName.toUpperCase());

            // CategoryType에 해당하는 Category 엔티티 찾기 (수정된 메서드명 사용)
            return categoryRepository.findByCategorytype(categoryType)
                    .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + categoryTypeName));
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 카테고리 타입인 경우
            throw new RuntimeException("유효하지 않은 카테고리 타입: " + categoryTypeName);
        }
    }

    /**
     * 모든 상품을 조회합니다.
     *
     * @return 상품 엔티티 목록
     */
    @Transactional(readOnly = true)
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 카테고리별 상품을 조회합니다.
     *
     * @param categoryType 카테고리 타입
     * @return 해당 카테고리의 상품 목록
     */
    @Transactional(readOnly = true)
    public List<Product> findProductsByCategory(String categoryType) {
        Category category = findCategoryByType(categoryType);
        return productRepository.findByCategory(category);
    }

    /**
     * 상품을 삭제합니다.
     *
     * @param id 삭제할 상품 ID
     */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
        log.info("상품 삭제 완료: ID={}", id);
    }
}