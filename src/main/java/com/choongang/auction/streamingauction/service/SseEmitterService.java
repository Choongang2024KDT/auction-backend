package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.responseDto.NotificationDto;
import com.choongang.auction.streamingauction.domain.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseEmitterService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(Long memberId, boolean unreadOnly) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        String key = getEmitterKey(memberId, unreadOnly);
        emitters.put(key, emitter);

        emitter.onCompletion(() -> emitters.remove(key));
        emitter.onTimeout(() -> emitters.remove(key));
        emitter.onError((e) -> emitters.remove(key));

        return emitter;
    }

    public void sendNotification(Long memberId, Notification notification) {
        sendToEmitter(memberId, notification, false); // 모든 알림
        if (!notification.isRead()) {
            sendToEmitter(memberId, notification, true); // 읽지 않은 알림
        }
    }

    private void sendToEmitter(Long memberId, Notification notification, boolean unreadOnly) {
        SseEmitter emitter = emitters.get(getEmitterKey(memberId, unreadOnly));
        if (emitter != null) {
            try {
                NotificationDto dto = toDto(notification);
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(dto));
            } catch (IOException e) {
                log.error("Failed to send SSE notification for memberId: {}, unreadOnly: {}", memberId, unreadOnly, e);
                emitters.remove(getEmitterKey(memberId, unreadOnly));
            }
        }
    }

    private String getEmitterKey(Long memberId, boolean unreadOnly) {
        return memberId + (unreadOnly ? "_unread" : "_all");
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