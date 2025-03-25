package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.responseDto.NotificationDto;
import com.choongang.auction.streamingauction.domain.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
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

    public SseEmitter createEmitter(Long memberId, boolean unreadOnly) {
        SseEmitter emitter = new SseEmitter(180000L); // 타임아웃 180초
        String key = getEmitterKey(memberId, unreadOnly);
        emitters.put(key, emitter);

        // 초기 연결 확인용 이벤트
        try {
            emitter.send(SseEmitter.event().name("connect").data("SSE connected"));
            log.info("초기 연결 시간 : {}",System.currentTimeMillis());
        } catch (IOException e) {
            log.error("Failed to send initial connect event for memberId: {}, unreadOnly: {}", memberId, unreadOnly, e);
            emitter.completeWithError(e);
        }

        // 주기적인 하트비트: 30초마다
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (emitters.containsKey(key)) { // emitter가 살아있는지 확인
                    log.info("Sending heartbeat for memberId: {}, unreadOnly: {}", memberId, unreadOnly);
                    emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
                } else {
                    log.warn("Emitter already removed for memberId: {}, unreadOnly: {}", memberId, unreadOnly);
                    scheduler.shutdown(); // emitter 없으면 스케줄러 종료
                }
            } catch (IOException e) {
                log.error("Failed to send heartbeat for memberId: {}, unreadOnly: {}", memberId, unreadOnly, e);
                emitter.completeWithError(e);
                emitters.remove(key);
                scheduler.shutdown(); // 스케줄러는 중단되지 않고 계속 실행하면 메모리 누수나 불필요한 로그를 유발
            }
        }, 0, 30, TimeUnit.SECONDS);

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

        // 클린업 처리
        // SseEmitter 객체가 더 이상 필요 없어질 때(연결이 끝나거나 문제가 생길 때) 서버에서 리소스를 정리하는 코드
        emitter.onCompletion(() -> {
            log.info("Emitter completed for memberId: {}, unreadOnly: {}", memberId, unreadOnly);
            emitters.remove(key);
        });
        emitter.onTimeout(() -> {
            log.info("Emitter timed out for memberId: {}, unreadOnly: {}", memberId, unreadOnly);
            emitters.remove(key);
        });
        emitter.onError((e) -> {
            log.error("Emitter error for memberId: {}, unreadOnly: {}", memberId, unreadOnly, e);
            emitters.remove(key);
        });

        return emitter;
    }

    public void sendNotification(Long memberId, Notification notification) {
        sendToEmitter(memberId, notification, false); // 모든 알림
        if (!notification.isRead()) {
            sendToEmitter(memberId, notification, true); // 읽지 않은 알림
        }
    }

    private void sendToEmitter(Long memberId, Notification notification, boolean unreadOnly) {
        String key = getEmitterKey(memberId, unreadOnly);
        SseEmitter emitter = emitters.get(key);
        if (emitter != null) {
            try {
                NotificationDto dto = toDto(notification);
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(dto));
            } catch (IOException e) {
                log.error("Failed to send SSE notification for memberId: {}, unreadOnly: {}", memberId, unreadOnly, e);
                emitters.remove(key);
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