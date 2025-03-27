package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.responseDto.NotificationDto;
import com.choongang.auction.streamingauction.domain.entity.Notification;
import com.choongang.auction.streamingauction.jwt.JwtTokenProvider;
import com.choongang.auction.streamingauction.service.NotificationService;
import com.choongang.auction.streamingauction.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public SseEmitter streamNotifications(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long memberId = getMemberIdFromToken(token);
        return sseEmitterService.createEmitter(memberId, false);
    }

    @GetMapping(value = "/stream/unread", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamUnreadNotifications(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long memberId = getMemberIdFromToken(token);
        return sseEmitterService.createEmitter(memberId, true);
    }

    @GetMapping
    public List<NotificationDto> getNotifications(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long memberId = getMemberIdFromToken(token);
        return notificationService.findAll(memberId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId,
                                           @RequestHeader("Authorization") String authHeader) {
        log.info("notificationId {}", notificationId);
        String token = authHeader.replace("Bearer ", "");
        notificationService.markAsRead(notificationId); // memberId 검증은 서비스에서 처리 가능
        return ResponseEntity.ok().build();
    }

    private Long getMemberIdFromToken(String token) {
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
