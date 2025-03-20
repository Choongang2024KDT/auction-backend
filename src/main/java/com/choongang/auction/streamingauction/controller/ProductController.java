package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.config.FileUploadConfig;
import com.choongang.auction.streamingauction.domain.product.domain.dto.ProductCreate;
import com.choongang.auction.streamingauction.domain.product.domain.dto.ProductDTO;
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

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/product")
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final JwtTokenProvider jwtTokenProvider;
    private final FileUploadConfig fileUploadConfig;
    // 상품 등록 (form-data를 통한 파일 업로드 지원)
    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> registerProduct(
            @RequestParam("productName") String productName,
            @RequestParam("productDescription") String productDescription,
            @RequestParam("productCategory") String productCategory,
            @RequestParam("productStartingPrice") Long productStartingPrice,
            @RequestParam("productBidIncrement") Long productBidIncrement,
            @RequestParam("productBuyNowPrice") Long productBuyNowPrice,
            @RequestParam(value = "images", required = false) List<MultipartFile> files,
            @RequestHeader("Authorization") String authHeader) {

        // 토큰에서 사용자 이름 추출
        String token = authHeader.replace("Bearer ", "");
        String username = jwtTokenProvider.getCurrentLoginUsername(token);

        log.info("상품 등록 요청: {}, 회원: {}", productName, username);
        log.info("카테고리: {}", productCategory);
        log.info("가격 정보: 시작가={}, 입찰단위={}, 즉시구매가={}",
                productStartingPrice, productBidIncrement, productBuyNowPrice);
        log.info("이미지 파일 개수: {}", files != null ? files.size() : 0);

        // 이미지 URL 생성 및 파일 저장
        List<String> imageUrls = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        // 고유한 파일명 생성
                        String originalFilename = file.getOriginalFilename();
                        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        String newFilename = UUID.randomUUID().toString() + extension;

                        // 저장 경로 설정 (FileUploadConfig에서 가져온 경로 사용)
                        String uploadDir = fileUploadConfig.getLocation();
                        File targetFile = new File(uploadDir + File.separator + newFilename);

                        // 파일 저장
                        file.transferTo(targetFile);

                        // URL 생성 (웹에서 접근 가능한 경로)
                        String imageUrl = "/uploads/" + newFilename;
                        imageUrls.add(imageUrl);

                        log.info("이미지 저장 및 URL 생성: {}", imageUrl);
                    } catch (IOException e) {
                        log.error("파일 저장 중 오류 발생", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                                "error", "파일 업로드 중 오류가 발생했습니다: " + e.getMessage()
                        ));
                    }
                }
            }
        }

        // 요청 파라미터로 DTO 객체 생성
        ProductCreate dto = new ProductCreate(
                productName,
                productDescription,
                productCategory,
                productStartingPrice,
                productBidIncrement,
                productBuyNowPrice,
                imageUrls.isEmpty() ? null : imageUrls.get(0)
        );

        // 서비스 계층을 통해 상품 저장 및 결과 DTO 받기
        ProductDTO savedProduct = productService.saveProductWithImages(dto, imageUrls, username);

        return ResponseEntity.ok().body(Map.of(
                "message", "상품이 등록되었습니다.",
                "product", savedProduct
        ));
    }

    // 상품 조회 요청
    @GetMapping("/{id}")
    public ResponseEntity<?> findProduct(@PathVariable Long id) {
        try {
            ProductDTO product = productService.findProduct(id);

            return ResponseEntity.ok().body(Map.of(
                    "message", "상품이 조회됐습니다.",
                    "product", product
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
        List<ProductDTO> products = productService.findAllProducts();

        return ResponseEntity.ok().body(Map.of(
                "message", "전체 상품 목록을 조회했습니다.",
                "products", products,
                "count", products.size()
        ));
    }

    // 카테고리별 상품 조회
    @GetMapping("/category/{categoryType}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable String categoryType) {
        try {
            List<ProductDTO> products = productService.findProductsByCategory(categoryType);

            return ResponseEntity.ok().body(Map.of(
                    "message", categoryType + " 카테고리의 상품 목록을 조회했습니다.",
                    "products", products,
                    "count", products.size()
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

        List<ProductDTO> products = productService.findProductsByMember(username);

        return ResponseEntity.ok().body(Map.of(
                "message", "내 상품 목록을 조회했습니다.",
                "products", products,
                "count", products.size()
        ));
    }

    // 특정 회원의 상품 목록 조회
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserProducts(@PathVariable String username) {
        List<ProductDTO> products = productService.findProductsByMember(username);

        return ResponseEntity.ok().body(Map.of(
                "message", username + " 회원의 상품 목록을 조회했습니다.",
                "products", products,
                "seller", username,
                "count", products.size()
        ));
    }

    // 상품 삭제 (관리자 권한)
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
}