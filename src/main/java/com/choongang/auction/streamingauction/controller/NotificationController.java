package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.responseDto.NotificationDto;
import com.choongang.auction.streamingauction.jwt.JwtTokenProvider;
import com.choongang.auction.streamingauction.jwt.entity.TokenUserInfo;
import com.choongang.auction.streamingauction.service.NotificationService;
import com.choongang.auction.streamingauction.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final SseEmitterService sseEmitterService;
    private final JwtTokenProvider jwtTokenProvider;

    // produces-> 응답이 text/event-stream임
    @GetMapping(value = "/stream/new", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNewNotifications(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        log.debug("/stream/new 엔드포인트 호출: tokenUserInfo {}", tokenUserInfo);
        if (tokenUserInfo == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        }
        Long memberId = tokenUserInfo.memberId();
        return sseEmitterService.createEmitter(memberId);
    }

    @GetMapping
    public List<NotificationDto> getNotifications(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        Long memberId = tokenUserInfo.memberId();
        return notificationService.findAll(memberId);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        notificationService.markAsRead(notificationId, tokenUserInfo.memberId()); // memberId 검증은 서비스에서 처리 가능
        return ResponseEntity.ok().build();
    }
}
