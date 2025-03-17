package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.category.entity.Category;
import com.choongang.auction.streamingauction.domain.category.entity.CategoryType;
import com.choongang.auction.streamingauction.domain.member.entity.Member;
import com.choongang.auction.streamingauction.domain.product.domain.dto.ProductCreate;
import com.choongang.auction.streamingauction.domain.product.domain.dto.ProductDTO;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import com.choongang.auction.streamingauction.domain.product.domain.entity.ProductImage;
import com.choongang.auction.streamingauction.domain.product.mapper.ProductMapper;
import com.choongang.auction.streamingauction.repository.CategoryRepository;
import com.choongang.auction.streamingauction.repository.MemberRepository;
import com.choongang.auction.streamingauction.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final ProductMapper productMapper; // ProductMapper 주입

    /**
     * 상품을 이미지와 함께 저장합니다.
     *
     * @param dto 상품 생성 DTO
     * @param imageUrls 이미지 URL 목록
     * @param username 로그인한 회원의 사용자명
     * @return 저장된 상품 정보를 담은 DTO
     */
    public ProductDTO saveProductWithImages(ProductCreate dto, List<String> imageUrls, String username) {
        log.info("상품 등록 시작: {}, 이미지 개수: {}, 회원: {}",
                dto.productName(),
                imageUrls != null ? imageUrls.size() : 0,
                username);

        // 회원 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다: " + username));
        log.info("회원 조회 결과: ID={}, 이름={}", member.getId(), member.getName());

        // 카테고리 조회
        Category category = findCategoryByType(dto.productCategory());
        log.info("카테고리 조회 결과: ID={}, Type={}",
                category.getCategoryId(), category.getCategoryType());

        // 상품 엔티티 생성 (가격 정보 포함)
        Product productEntity = Product.builder()
                .name(dto.productName())
                .description(dto.productDescription())
                .category(category)
                .categoryName(category.getCategoryType().name()) // 카테고리명 직접 설정
                .member(member)
                .startingPrice(dto.productStartingPrice())
                .bidIncrease(dto.productBidIncrease())
                .buyNowPrice(dto.productBuyNowPrice())
                .images(new ArrayList<>())
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

        log.info("저장된 상품: ID={}, 카테고리={}, 이미지 개수={}, 회원={}",
                savedProduct.getProductId(),
                savedProduct.getCategory().getCategoryType(),
                savedProduct.getImages().size(),
                savedProduct.getMember().getUsername());

        // ProductMapper를 사용하여 DTO 변환
        return productMapper.toDto(savedProduct);
    }

    /**
     * ID로 상품을 조회합니다.
     *
     * @param id 상품 ID
     * @return 조회된 상품 정보를 담은 DTO
     */
    @Transactional(readOnly = true)
    public ProductDTO findProduct(Long id) {
        // 이미지를 함께 조회하는 쿼리 사용
        Product product = productRepository.findByIdWithImages(id)
                .orElseGet(() -> productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다. ID: " + id)));

        log.info("상품 조회: ID={}, 카테고리={}, 이미지 개수={}",
                product.getProductId(),
                product.getCategory().getCategoryType(),
                product.getImages().size());

        // ProductMapper를 사용하여 DTO 변환
        return productMapper.toDto(product);
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

            // CategoryType에 해당하는 Category 엔티티 찾기
            return categoryRepository.findByCategoryType(categoryType)
                    .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다: " + categoryTypeName));
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 카테고리 타입인 경우
            throw new RuntimeException("유효하지 않은 카테고리 타입: " + categoryTypeName);
        }
    }

    /**
     * 모든 상품을 조회합니다.
     *
     * @return 상품 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findAllProducts() {
        // ProductMapper를 사용하여 DTO 목록 변환
        return productMapper.toDtoList(productRepository.findAll());
    }

    /**
     * 카테고리별 상품을 조회합니다.
     *
     * @param categoryType 카테고리 타입
     * @return 해당 카테고리의 상품 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findProductsByCategory(String categoryType) {
        Category category = findCategoryByType(categoryType);
        // ProductMapper를 사용하여 DTO 목록 변환
        return productMapper.toDtoList(productRepository.findByCategory(category));
    }

    /**
     * 회원별 상품을 조회합니다.
     *
     * @param username 회원 사용자명
     * @return 해당 회원이 등록한 상품 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findProductsByMember(String username) {
        // ProductMapper를 사용하여 DTO 목록 변환
        return productMapper.toDtoList(productRepository.findByMemberUsername(username));
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

    /**
     * 회원이 등록한 상품을 삭제합니다.
     * 상품 소유자와 삭제 요청자가 일치하는지 확인합니다.
     *
     * @param productId 삭제할 상품 ID
     * @param username 삭제 요청자 사용자명
     */
    public void deleteProductByMember(Long productId, String username) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다. ID: " + productId));

        // 상품 소유자 확인
        if (!product.getMember().getUsername().equals(username)) {
            throw new RuntimeException("상품 삭제 권한이 없습니다. 상품 소유자만 삭제할 수 있습니다.");
        }

        productRepository.delete(product);
        log.info("회원 상품 삭제 완료: 상품ID={}, 회원={}", productId, username);
    }
}