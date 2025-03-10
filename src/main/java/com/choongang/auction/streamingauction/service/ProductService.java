package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.entity.Product;
import com.choongang.auction.streamingauction.domain.dto.ProductCreate;
import com.choongang.auction.streamingauction.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional // JPA에서 필수
public class ProductService {

    private final ProductRepository productRepository;

    //상품 등록
    public void saveProduct(ProductCreate dto) {

        Product productEntity = Product.builder()
                .name(dto.productName())
                .description(dto.productDescription())
                .category(dto.productCategory())
                .imageUrl(dto.productImageUrl())
//                .createdAt(LocalDateTime.now())
                .build();

        productRepository.save(productEntity);
    }

    //상품 조회
    public Product findProduct (Long id) {
        Product product = productRepository.findById(id).orElseThrow(); //null값 처리
        return product;
    }

}
