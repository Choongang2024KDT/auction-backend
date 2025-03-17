package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.requestDto.AuctionRequestDto;
import com.choongang.auction.streamingauction.domain.dto.responseDto.AuctionResponseDto;
import com.choongang.auction.streamingauction.domain.entity.Auction;
import com.choongang.auction.streamingauction.service.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auction")
public class AuctionController {

    private final AuctionService auctionService;

    //경매 생성 요청
    @PostMapping("/createAuction")
    public ResponseEntity<?> creatAuction(
            @RequestBody AuctionRequestDto auctionRequestDto
            ){
        auctionService.createAuction(auctionRequestDto);
        return ResponseEntity.ok().body(Map.of(
                "message" , "경매가 시작되었습니다."
        ));
    }

    //경매 조회 요청
    @GetMapping("/{productId}")
    public ResponseEntity<?> getAuctionInfo(
            @PathVariable Long productId
    ){
        AuctionResponseDto auctionInfo = auctionService.getAuctionInfo(productId);
        return ResponseEntity.ok().body(Map.of(
                "message", "경매 조회 요청에 성공했습니다.",
                "auctionInfo" , auctionInfo
        ));
    }
}
