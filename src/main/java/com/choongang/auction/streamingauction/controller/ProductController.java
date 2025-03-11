package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.requestDto.ProductCreate;
import com.choongang.auction.streamingauction.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/product")
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    //상품 등록 요청
    @PostMapping("/post")
    public ResponseEntity<?> postProduct(
            @RequestBody ProductCreate dto
            ){
        productService.saveProduct(dto);
        return ResponseEntity.ok().body(Map.of(
                "message", "상품이 등록되었습니다."
        ));
    }
}
