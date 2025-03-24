package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.responseDto.NotificationDto;
import com.choongang.auction.streamingauction.domain.entity.Notification;
import com.choongang.auction.streamingauction.jwt.JwtTokenProvider;
import com.choongang.auction.streamingauction.service.NotificationService;
import com.choongang.auction.streamingauction.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final SseEmitterService sseEmitterService;
    private final JwtTokenProvider jwtTokenProvider; // JWT 검증용 추가 (가정)

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@RequestParam("token") String token) {
        Long memberId = getMemberIdFromToken(token); // 토큰에서 memberId 추출
        return sseEmitterService.createEmitter(memberId, false);
    }

    @GetMapping(value = "/stream/unread", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamUnreadNotifications(@RequestParam("token") String token) {
        Long memberId = getMemberIdFromToken(token);
        return sseEmitterService.createEmitter(memberId, true);
    }

    @GetMapping
    public List<NotificationDto> getNotifications() {
        Long memberId = getCurrentMemberId();
        return notificationService.findAll(memberId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{notificationId}/read")
    public void markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
    }

    private Long getCurrentMemberId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Long getMemberIdFromToken(String token) {
        // JWT 토큰 검증 및 memberId 추출 (예시)
        if (jwtTokenProvider.validateToken(token)) {
            return jwtTokenProvider.getCurrentMemberId(token);
        }
        throw new IllegalArgumentException("Invalid or expired token");
    }

    private NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .notificationId(notification.getNotificationId())
                .message(notification.getMessage())
                .link(notification.getLink())
                .isRead(notification.isRead())
                .safeNumber(notification.getSafeNumber())
                .build();
    }
}
