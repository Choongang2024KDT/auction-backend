package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
@Slf4j
public class SseController {
    private final SseEmitterService sseEmitterService;

    // 특정 사용자(userId)가 SSE 구독
    @GetMapping("/subscribe/{userId}")
    public SseEmitter subscribe(@PathVariable Long userId) {

        // 로그인 시스템 구축 되면 사용
        // JWT 또는 세션에서 현재 로그인된 사용자 ID 가져오기
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Long currentUserId = Long.parseLong(authentication.getName());

        log.info("구독하는 유저 id: {}", userId);

        return sseEmitterService.subscribe(userId);
    }
}