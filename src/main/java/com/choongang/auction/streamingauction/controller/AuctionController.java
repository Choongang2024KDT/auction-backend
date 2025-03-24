package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.requestDto.AuctionRequestDto;
import com.choongang.auction.streamingauction.domain.dto.responseDto.AuctionResponseDto;
import com.choongang.auction.streamingauction.domain.entity.Auction;
import com.choongang.auction.streamingauction.jwt.JwtTokenProvider;
import com.choongang.auction.streamingauction.service.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final JwtTokenProvider jwtTokenProvider;

    //경매 생성 요청
    @PostMapping("/createAuction")
    public ResponseEntity<?> createAuction(
            @RequestBody AuctionRequestDto auctionRequestDto
            ){
        AuctionResponseDto auctionInfo = auctionService.createAuction(auctionRequestDto);
        return ResponseEntity.ok().body(auctionInfo);
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

    // 판매자가 경매 종료 요청
    @PostMapping("/closeAuction")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> closeAuction(
           @RequestBody AuctionRequestDto auctionRequestDto,
           @RequestHeader("Authorization") String authHeader
    ){
        //요청받은 토큰에서 memberId 찾기
        String token = authHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getCurrentMemberId(token);

        auctionService.closeAuctionBySeller(auctionRequestDto , memberId);

        return ResponseEntity.ok().body(Map.of(
                "message", "경매가 종료되었습니다."
        ));
    }

}
