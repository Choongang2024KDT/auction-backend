package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.product.domain.dto.ProductCreate;
import com.choongang.auction.streamingauction.product.domain.entity.Product;
import com.choongang.auction.streamingauction.product.domain.entity.ProductImage;
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
            @RequestParam(value = "images", required = false) MultipartFile[] files) {

        log.info("상품 등록 요청: {}", productName);
        log.info("이미지 파일 개수: {}", files != null ? files.length : 0);

        List<String> imageUrls = new ArrayList<>();

        // 업로드 디렉토리 생성
        try {
            createUploadDirectoryIfNeeded();
        } catch (IOException e) {
            log.error("업로드 디렉토리 생성 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "message", "파일 저장 경로를 생성할 수 없습니다."
            ));
        }

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
                firstImageUrl
        );

        Product savedProduct = productService.saveProductWithImages(dto, imageUrls);

        return ResponseEntity.ok().body(Map.of(
                "message", "상품이 등록되었습니다.",
                "productId", savedProduct.getProductId(),
                "imageUrls", imageUrls
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

    // 업로드 디렉토리 생성
    private void createUploadDirectoryIfNeeded() throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("업로드 디렉토리 생성: {}", uploadPath.toAbsolutePath());
        }
    }
}