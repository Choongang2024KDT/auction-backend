package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.responseDto.NotificationDto;
import com.choongang.auction.streamingauction.jwt.JwtTokenProvider;
import com.choongang.auction.streamingauction.jwt.entity.TokenUserInfo;
import com.choongang.auction.streamingauction.service.NotificationService;
import com.choongang.auction.streamingauction.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final SseEmitterService sseEmitterService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        Long memberId = tokenUserInfo.memberId();
        return sseEmitterService.createEmitter(memberId, false);
    }

    @GetMapping(value = "/stream/unread", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamUnreadNotifications(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        Long memberId = tokenUserInfo.memberId();
        return sseEmitterService.createEmitter(memberId, true);
    }

    // 로그인 이후에 얘가 제일 먼저 호출?
    @GetMapping
    public List<NotificationDto> getNotifications(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        log.info("Notification 목록 호출 시 token: {}", token);
        Long memberId = jwtTokenProvider.getCurrentMemberId(token);
        return notificationService.findAll(memberId);
    }

//    @GetMapping
//    public List<NotificationDto> getNotifications(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
//        Long memberId = tokenUserInfo.memberId();
//        return notificationService.findAll(memberId);
//    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId); // memberId 검증은 서비스에서 처리 가능
        return ResponseEntity.ok().build();
    }
}
