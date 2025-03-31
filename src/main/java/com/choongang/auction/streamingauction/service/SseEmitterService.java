package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.responseDto.NotificationDto;
import com.choongang.auction.streamingauction.domain.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;


// 이상한 거 너무 많음 RTC 작업 이후 수정하기...
@Service
@Slf4j
public class SseEmitterService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(Long memberId) {
        // 새 emitter
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 타임아웃 60분
        String key = getEmitterKey(memberId);
        emitters.put(key, emitter);

        log.info("Created emitter for key: {}, Current emitters map: {}", key, emitters.keySet());

        // 초기 연결 확인용 이벤트
        try {
            emitter.send(SseEmitter.event().name("connect").data("SSE connected"));
            log.info("초기 연결 시간 : {}",System.currentTimeMillis());
        } catch (IOException e) {
            log.error("Failed to send initial connect event for memberId: {}", memberId);
            emitter.completeWithError(e);
        }

        // 주기적인 하트비트: 30초마다
//        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//        scheduler.scheduleAtFixedRate(() -> {
//            try {
//                if (emitters.containsKey(key)) { // emitter가 살아있는지 확인
//                    log.info("Sending heartbeat for memberId: {}", memberId);
//                    emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
//                } else {
//                    log.warn("Emitter already removed for memberId: {}", memberId);
//                    scheduler.shutdown(); // emitter 없으면 스케줄러 종료
//                }
//            } catch (IOException e) {
//                log.warn("Failed to send heartbeat for memberId: {}", memberId, e);
//                emitter.completeWithError(e);
//                emitters.remove(key);
//                scheduler.shutdown(); // 스케줄러는 중단되지 않고 계속 실행하면 메모리 누수나 불필요한 로그를 유발
//            }
//        }, 0, 30, TimeUnit.SECONDS);

        // 클린업 처리
        // SseEmitter 객체가 더 이상 필요 없어질 때(연결이 끝나거나 문제가 생길 때) 서버에서 리소스를 정리하는 코드
        emitter.onCompletion(() -> {
            if (emitters.get(key) == emitter) { // 현재 emitter가 나인지 확인
                emitters.remove(key);
                log.info("Emitter completed for key: {}", key);
            }
        });
        emitter.onTimeout(() -> {
            if (emitters.get(key) == emitter) {
                emitters.remove(key);
                log.debug("Emitter timed out for key: {}", key);
            }
        });
        emitter.onError((e) -> {
            if (emitters.get(key) == emitter) {
                emitters.remove(key);
                log.error("Emitter error for key: {}", key, e);
            }
        });

        return emitter;
    }

    public void sendToEmitter(Long memberId, Notification notification) {
        String key = getEmitterKey(memberId);
        SseEmitter emitter = emitters.get(key);

        if (emitter != null) {
            try {
                NotificationDto dto = toDto(notification);
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(dto));
            } catch (IOException e) {
                log.error("Failed to send SSE notification for memberId: {}", memberId, e);
                emitters.remove(key);
            }
        }
    }

    private String getEmitterKey(Long memberId) {
        return String.valueOf(memberId);
    }

    public void disconnectOnLogout(Long memberId) {
        String key = getEmitterKey(memberId);
        SseEmitter emitter = emitters.remove(key);
        if (emitter != null) {
            try {
                emitter.complete();
                log.info("Emitter for key: {} terminated on logout", key);
            } catch (IllegalStateException e) {
                log.debug("Emitter already completed for key: {}", key);
            }
        }
    }

    private NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .notificationId(notification.getNotificationId())
                .message(notification.getMessage())
                .link(notification.getLink())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt().toString())
                .safeNumber(notification.getSafeNumber())
                .build();
    }
}