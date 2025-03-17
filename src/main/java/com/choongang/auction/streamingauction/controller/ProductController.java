package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.product.domain.dto.ProductCreate;

import com.choongang.auction.streamingauction.domain.product.domain.dto.ProductDTO;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import com.choongang.auction.streamingauction.domain.product.domain.entity.ProductImage;
import com.choongang.auction.streamingauction.jwt.JwtTokenProvider;
import com.choongang.auction.streamingauction.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product")
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final JwtTokenProvider jwtTokenProvider;

    // 상품 등록 (form-data를 통한 파일 업로드 지원)
    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> registerProduct(
            @RequestParam("productName") String productName,
            @RequestParam("productDescription") String productDescription,
            @RequestParam("productCategory") String productCategory,
            @RequestParam("productStartingPrice") Long productStartingPrice,
            @RequestParam("productBidIncrease") Long productBidIncrease,
            @RequestParam("productBuyNowPrice") Long productBuyNowPrice,
            @RequestParam(value = "images", required = false) MultipartFile[] files,
            @RequestHeader("Authorization") String authHeader) {

        // 토큰에서 사용자 이름 추출
        String token = authHeader.replace("Bearer ", "");
        String username = jwtTokenProvider.getCurrentLoginUsername(token);

        log.info("상품 등록 요청: {}, 회원: {}", productName, username);
        log.info("카테고리: {}", productCategory);
        log.info("가격 정보: 시작가={}, 입찰단위={}, 즉시구매가={}",
                productStartingPrice, productBidIncrease, productBuyNowPrice);
        log.info("이미지 파일 개수: {}", files != null ? files.length : 0);

        List<String> imageUrls = new ArrayList<>();

        // 파일 업로드 처리 (DB에 URL만 저장)
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // 고유한 파일명 생성
                    String originalFilename = file.getOriginalFilename();
                    String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    String newFilename = UUID.randomUUID().toString() + extension;

                    // 파일은 저장하지 않고 URL만 생성
                    String imageUrl = "/uploads/" + newFilename;
                    imageUrls.add(imageUrl);

                    log.info("이미지 URL 생성: {}", imageUrl);
                }
            }
        }

        // DTO 생성 및 저장
        ProductCreate dto = new ProductCreate(
                productName,
                productDescription,
                productCategory,
                productStartingPrice,
                productBidIncrease,
                productBuyNowPrice,
                imageUrls.isEmpty() ? null : imageUrls.get(0)
        );

        // 회원 정보와 함께 상품 저장
        Product savedProduct = productService.saveProductWithImages(dto, imageUrls, username);

        return ResponseEntity.ok().body(Map.of(
                "message", "상품이 등록되었습니다.",
                "product", convertToDTO(savedProduct)
        ));
    }

    // 상품 조회 요청
    @GetMapping("/{id}")
    public ResponseEntity<?> findProduct(@PathVariable Long id) {
        try {
            Product product = productService.findProduct(id);

            return ResponseEntity.ok().body(Map.of(
                    "message", "상품이 조회됐습니다.",
                    "product", convertToDTO(product)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "상품을 찾을 수 없습니다: " + e.getMessage()
            ));
        }
    }

    // 모든 상품 조회
    @GetMapping("/all")
    public ResponseEntity<?> getAllProducts() {
        List<Product> products = productService.findAllProducts();

        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(Map.of(
                "message", "전체 상품 목록을 조회했습니다.",
                "products", productDTOs,
                "count", productDTOs.size()
        ));
    }

    // 카테고리별 상품 조회
    @GetMapping("/category/{categoryType}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable String categoryType) {
        try {
            List<Product> products = productService.findProductsByCategory(categoryType);

            List<ProductDTO> productDTOs = products.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok().body(Map.of(
                    "message", categoryType + " 카테고리의 상품 목록을 조회했습니다.",
                    "products", productDTOs,
                    "count", productDTOs.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "카테고리 상품 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    // 내 상품 목록 조회 (로그인한 회원)
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyProducts(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtTokenProvider.getCurrentLoginUsername(token);

        List<Product> products = productService.findProductsByMember(username);

        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(Map.of(
                "message", "내 상품 목록을 조회했습니다.",
                "products", productDTOs,
                "count", productDTOs.size()
        ));
    }

    // 특정 회원의 상품 목록 조회
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserProducts(@PathVariable String username) {
        List<Product> products = productService.findProductsByMember(username);

        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(Map.of(
                "message", username + " 회원의 상품 목록을 조회했습니다.",
                "products", productDTOs,
                "seller", username,
                "count", productDTOs.size()
        ));
    }

    // 상품 삭제
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().body(Map.of(
                "message", "상품이 삭제되었습니다.",
                "productId", id
        ));
    }

    // 내 상품 삭제 (로그인한 회원)
    @DeleteMapping("/my/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteMyProduct(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String username = jwtTokenProvider.getCurrentLoginUsername(token);

        try {
            productService.deleteProductByMember(id, username);
            return ResponseEntity.ok().body(Map.of(
                    "message", "상품이 삭제되었습니다.",
                    "productId", id
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    // Product 엔티티를 DTO로 변환
    private ProductDTO convertToDTO(Product product) {
        List<String> imageUrls = product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        return ProductDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .startingPrice(product.getStartingPrice())
                .bidIncrease(product.getBidIncrease())
                .buyNowPrice(product.getBuyNowPrice())
                .categoryType(product.getCategory().getCategoryType().name())
                .sellerUsername(product.getMember() != null ? product.getMember().getUsername() : null)
                .imageUrls(imageUrls)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}