package com.choongang.auction.streamingauction.controller;


import com.choongang.auction.streamingauction.domain.dto.requestDto.BidRequestDto;
import com.choongang.auction.streamingauction.domain.entity.Bid;
import com.choongang.auction.streamingauction.service.BidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/bid")
@RequiredArgsConstructor
@Slf4j
public class BidController {

    private final BidService bidService;
    //입찰 기능
    @PostMapping("/tender")
    public ResponseEntity<?> saveBid(
            @RequestBody BidRequestDto bidRequestDto
            ) {
        bidService.saveBid(bidRequestDto);
        return ResponseEntity.ok().body(Map.of(
               "message", "입찰에 성공하셨습니다."
        ));
    }

    //현재 최고가 입찰금액과 유저의 id 조회 요청
    @GetMapping("/maxBid")
    public ResponseEntity<?> getMaxBid() {
        try {
            Bid MaxBid = bidService.getMaxBid();  // 서비스에서 데이터를 받음
            return ResponseEntity.ok(MaxBid); // 200 OK 응답
        } catch (NoSuchElementException ex) {
            // 예외 처리 - 입찰이 없으면 404 응답
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // 404 Not Found 응답
        }
    }
}
