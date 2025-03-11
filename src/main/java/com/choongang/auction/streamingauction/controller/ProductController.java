package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.product.domain.dto.ProductCreate;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import com.choongang.auction.streamingauction.domain.product.domain.entity.ProductImage;
import com.choongang.auction.streamingauction.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product")
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 실제 파일 저장 디렉토리 (실제 프로젝트에 맞게 수정 필요)
    private final String uploadDir = "uploads";

    // 상품 등록 (form-data를 통한 파일 업로드 지원)
    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerProduct(
            @RequestParam("productName") String productName,
            @RequestParam("productDescription") String productDescription,
            @RequestParam("productCategory") String productCategory,
            @RequestParam("productPrice") Long productPrice,
            @RequestParam(value = "images", required = false) MultipartFile[] files) {

        log.info("상품 등록 요청: {}", productName);
        log.info("카테고리: {}", productCategory);
        log.info("이미지 파일 개수: {}", files != null ? files.length : 0);

        List<String> imageUrls = new ArrayList<>();



        // 파일 업로드 처리
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        // 고유한 파일명 생성
                        String originalFilename = file.getOriginalFilename();
                        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        String newFilename = UUID.randomUUID().toString() + extension;

                        // 파일 저장
                        String filePath = uploadDir + File.separator + newFilename;
                        Path path = Paths.get(filePath);
                        Files.write(path, file.getBytes());

                        // 이미지 URL 생성 (웹에서 접근 가능한 경로)
                        String imageUrl = "/uploads/" + newFilename;
                        imageUrls.add(imageUrl);

                        log.info("파일 저장 성공: {}", imageUrl);
                    } catch (IOException e) {
                        log.error("파일 저장 실패", e);
                    }
                }
            }
        }

        // DTO 생성 및 저장 (첫 번째 이미지만 DTO에 포함)
        String firstImageUrl = imageUrls.isEmpty() ? null : imageUrls.get(0);
        ProductCreate dto = new ProductCreate(
                productName,
                productDescription,
                productCategory,
                productPrice,
                firstImageUrl
        );

        Product savedProduct = productService.saveProductWithImages(dto, imageUrls);

        return ResponseEntity.ok().body(Map.of(
                "message", "상품이 등록되었습니다.",
                "productId", savedProduct.getProductId(),
                "categoryId", savedProduct.getCategory().getCategoryId(),
                "categoryType", savedProduct.getCategory().getCategorytype().name()
        ));
    }

    // 상품 조회 요청
    @GetMapping("/{id}")
    public ResponseEntity<?> findProduct(@PathVariable Long id) {
        Product product = productService.findProduct(id);

        // 이미지 URL만 추출하여 응답에 포함
        List<String> imageUrls = product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(Map.of(
                "message", "상품이 조회됐습니다.",
                "product", product,
                "imageUrls", imageUrls
        ));
    }

    // 모든 상품 조회
    @GetMapping("/all")
    public ResponseEntity<?> getAllProducts() {
        List<Product> products = productService.findAllProducts();
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
            List<Product> products = productService.findProductsByCategory(categoryType);
            return ResponseEntity.ok().body(Map.of(
                    "message", categoryType + " 카테고리의 상품 목록을 조회했습니다.",
                    "products", products,
                    "count", products.size()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    // 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok().body(Map.of(
                    "message", "상품이 삭제되었습니다.",
                    "productId", id
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }



}