package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.responseDto.TradeRecordDTO;
import com.choongang.auction.streamingauction.service.TradeRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/tradeRecord")
@RequiredArgsConstructor
public class TradeRecordController {
    private final TradeRecordService tradeRecordService;

    @GetMapping("/sales")
    public ResponseEntity<List<TradeRecordDTO>> getSales(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "latest") String sortBy) {
        return ResponseEntity.ok(tradeRecordService.getSales(userId, sortBy));
    }

    @GetMapping("/purchases")
    public ResponseEntity<List<TradeRecordDTO>> getPurchases(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "latest") String sortBy) {
        return ResponseEntity.ok(tradeRecordService.getPurchases(userId, sortBy));
    }
}
