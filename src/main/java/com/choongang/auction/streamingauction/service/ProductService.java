package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.product.domain.dto.ProductCreate;
import com.choongang.auction.streamingauction.product.domain.entity.Product;
import com.choongang.auction.streamingauction.product.domain.entity.ProductImage;
import com.choongang.auction.streamingauction.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    // 여러 이미지와 함께 상품 등록
    public Product saveProductWithImages(ProductCreate dto, List<String> imageUrls) {
        log.info("상품 등록 시작: {}, 이미지 개수: {}", dto.productName(), imageUrls != null ? imageUrls.size() : 0);

        // 상품 엔티티 생성
        Product productEntity = Product.builder()
                .name(dto.productName())
                .description(dto.productDescription())
                .category(dto.productCategory())
                .build();

        // 이미지 URL 처리
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    ProductImage image = ProductImage.builder()
                            .imageUrl(imageUrl)
                            .product(productEntity)
                            .build();

                    productEntity.addImage(image);
                    log.info("이미지 추가: {}", imageUrl);
                }
            }
        }

        Product savedProduct = productRepository.save(productEntity);
        log.info("저장된 상품: {}, 이미지 개수: {}", savedProduct.getProductId(), savedProduct.getImages().size());

        return savedProduct;
    }

    // 상품 조회
    @Transactional(readOnly = true)
    public Product findProduct(Long id) {
        // 이미지를 함께 조회하는 쿼리 사용
        Product product = productRepository.findByIdWithImages(id)
                .orElseGet(() -> productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다. ID: " + id)));

        log.info("상품 조회: {}, 이미지 개수: {}", product.getProductId(), product.getImages().size());

        return product;
    }
}