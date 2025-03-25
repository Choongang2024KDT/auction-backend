package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.responseDto.NotificationDto;
import com.choongang.auction.streamingauction.domain.entity.Notification;
import com.choongang.auction.streamingauction.exception.ErrorCode;
import com.choongang.auction.streamingauction.exception.SseException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SseEmitterService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // 단일 스레드 풀

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(this::sendHeartbeats, 0, 10, TimeUnit.SECONDS);
    }

    public synchronized SseEmitter createEmitter(Long memberId, boolean unreadOnly) {
        String key = getEmitterKey(memberId, unreadOnly);
;
        log.info("Initial emitters: {} terminated", emitters.keySet());

        // 기존 emitter 종료
        SseEmitter oldEmitter = emitters.remove(key);
        if (oldEmitter != null) {
            oldEmitter.complete();
            log.info("Old emitter for key: {} terminated", key);
        }

        // 새 emitter
        SseEmitter emitter = new SseEmitter(180_000L); // 타임아웃 180초
        emitters.put(key, emitter);
        log.info("Created emitter for key: {}, Current emitters map: {}", key, emitters.keySet());

        // 초기 연결 확인용 이벤트
        try {
            emitter.send(SseEmitter.event().name("connect").data("SSE connected"));
            log.info("Initial connect event sent ...");
        } catch (IOException e) {
            log.error("Initial connect failed for key: {}", key, e);
            emitter.complete();
            emitters.remove(key);
            throw new SseException(ErrorCode.SSE_CONNECTION_FAILED, "Initial SSE connection failed");
        }

        // 클린업 처리
        // SseEmitter 객체가 더 이상 필요 없어질 때(연결이 끝나거나 문제가 생길 때) 서버에서 리소스를 정리하는 코드
        emitter.onCompletion(() -> {
            emitters.remove(key);
            log.info("Emitter completed for key: {}", key);
        });
        emitter.onTimeout(() -> {
            emitters.remove(key);
            log.info("Emitter timed out for key: {}", key);
        });
        emitter.onError(e -> {
            emitters.remove(key);
            log.error("Emitter error for key: {}", key, e);
        });

        return emitter;
    }

        // 비동기 작업 대신 emitter 내부에서 처리
//        Executors.newSingleThreadExecutor().execute(() -> {
//            while (emitters.containsKey(key)) {
//                try {
//                    log.info("Sending heartbeat for memberId: {}, unreadOnly: {}", memberId, unreadOnly);
//                    emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
//                    Thread.sleep(15000); // 15초 대기
//                } catch (IOException e) {
//                    log.error("Failed to send heartbeat for memberId: {}, unreadOnly: {}", memberId, unreadOnly, e);
//                    emitter.completeWithError(e);
//                    emitters.remove(key);
//                    break;
//                } catch (InterruptedException e) {
//                    log.error("Heartbeat thread interrupted for memberId: {}", memberId, e);
//                    break;
//                }
//            }
//        });

        // Spring의 AsyncTaskExecutor 사용
//        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
//        executor.execute(() -> {
//            while (emitters.containsKey(key)) {
//                try {
//                    log.info("Sending heartbeat for memberId: {}, unreadOnly: {}, time: {}",
//                            memberId, unreadOnly, System.currentTimeMillis());
//                    emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
//                    log.info("Heartbeat sent successfully for memberId: {}, unreadOnly: {}, time: {}",
//                            memberId, unreadOnly, System.currentTimeMillis());
//                    Thread.sleep(15000); // 15초 대기
//                } catch (IOException e) {
//                    log.error("Failed to send heartbeat for memberId: {}, unreadOnly: {}", memberId, unreadOnly, e);
//                    emitter.completeWithError(e);
//                    emitters.remove(key);
//                    break;
//                } catch (InterruptedException e) {
//                    log.error("Heartbeat thread interrupted for memberId: {}", memberId, e);
//                    break;
//                }
//            }
//        });

        // 동일 컨텍스트에서 heartbeat 실행
//        new Thread(() -> {
//            while (emitters.containsKey(key)) {
//                try {
//                    log.info("Sending heartbeat for memberId: {}, unreadOnly: {}, time: {}",
//                            memberId, unreadOnly, System.currentTimeMillis());
//                    emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
//                    log.info("Heartbeat sent successfully for memberId: {}, unreadOnly: {}, time: {}",
//                            memberId, unreadOnly, System.currentTimeMillis());
//                    Thread.sleep(15000); // 15초 대기
//                } catch (IOException e) {
//                    log.error("Failed to send heartbeat for memberId: {}, unreadOnly: {}", memberId, unreadOnly, e);
//                    emitter.completeWithError(e);
//                    emitters.remove(key);
//                    break;
//                } catch (InterruptedException e) {
//                    log.error("Heartbeat thread interrupted for memberId: {}", memberId, e);
//                    break;
//                }
//            }
//        }).start();

    private void sendHeartbeats() {
        emitters.forEach((key, emitter) -> {
            try {
                log.info("Sending heartbeat for key: {}", key);
                emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
            } catch (IOException e) {
                log.error("Failed to send heartbeat for key: {}", key, e);
                emitter.complete();
                emitters.remove(key);
            }
        });
    }

    public void sendNotification(Long memberId, Notification notification) {
        sendToEmitter(memberId, notification, false); // 모든 알림
        if (!notification.isRead()) {
            sendToEmitter(memberId, notification, true); // 읽지 않은 알림
        }
        log.info("{}번 회원에게 알림번호{} 전송", memberId, notification);
    }

    private void sendToEmitter(Long memberId, Notification notification, boolean unreadOnly) {
        String key = getEmitterKey(memberId, unreadOnly);
        SseEmitter emitter = emitters.get(key);
        if (emitter != null) {
            try {
                NotificationDto dto = toDto(notification);
                emitter.send(SseEmitter.event().name("notification").data(dto));
            } catch (IOException e) {
                log.error("Failed to send SSE notification for memberId: {}, unreadOnly: {}", memberId, unreadOnly, e);
                emitters.remove(key);
                throw new SseException(ErrorCode.NOTIFICATION_SEND_FAILED, "Notification send failed");
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
                .createdAt(notification.getCreatedAt().toString())
                .safeNumber(notification.getSafeNumber())
                .build();
    }
}