package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.requestDto.AuctionRequestDto;
import com.choongang.auction.streamingauction.service.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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


}
