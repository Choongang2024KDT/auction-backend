package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.responseDto.TradeRecordDTO;
import com.choongang.auction.streamingauction.domain.member.entity.Member;
import com.choongang.auction.streamingauction.jwt.JwtTokenProvider;
import com.choongang.auction.streamingauction.repository.MemberRepository;
import com.choongang.auction.streamingauction.service.MemberService;
import com.choongang.auction.streamingauction.service.TradeRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tradeRecord")
@RequiredArgsConstructor
public class TradeRecordController {
    private final TradeRecordService tradeRecordService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/sales")
    public ResponseEntity<List<TradeRecordDTO>> getSales(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "latest") String sortBy) {

        String token = authorization.replace("Bearer ", "");

        // 토큰에서 userId 가져오기
        // 지금 토큰에 userId 포함 안 시키는 상태라 임시로...
        String username = jwtTokenProvider.getCurrentLoginUsername(token);
        // username으로 id 가져오는 임시코드 변경 필수
        Optional<Member> foundmember = memberRepository.findByUsername(username);
        Long memberId = foundmember.get().getId();
        //

        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(tradeRecordService.getSales(memberId, sortBy));
    }

    @GetMapping("/purchases")
    public ResponseEntity<List<TradeRecordDTO>> getPurchases(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "latest") String sortBy) {

        String token = authorization.replace("Bearer ", "");

        // 토큰에서 userId 가져오기
        // 지금 토큰에 userId 포함 안 시키는 상태라 임시로...
        String username = jwtTokenProvider.getCurrentLoginUsername(token);
        // username으로 id 가져오는 임시코드
        Optional<Member> foundmember = memberRepository.findByUsername(username);
        Long memberId = foundmember.get().getId();
        //

        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(tradeRecordService.getPurchases(memberId, sortBy));
    }
}
