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

// 이상한 거 너무 많음 RTC 작업 이후 수정하기...
@Service
@Slf4j
public class SseEmitterService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // 단일 스레드 풀

    // heartbeat 주기 설정
    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(this::sendHeartbeats, 0, 20, TimeUnit.SECONDS);
    }

    public SseEmitter createEmitter(Long memberId) {
        String key = getEmitterKey(memberId);
;
        log.info("Initial emitters: {} terminated", emitters.keySet());

        // 기존 emitter 종료
        SseEmitter oldEmitter = emitters.get(key);
        if (oldEmitter != null) {
            oldEmitter.complete();
            log.info("Old emitter for key: {} terminated", key);
        }

        // 새 emitter
        SseEmitter emitter = new SseEmitter(3_600_000L); // 타임아웃 1시간
        emitters.put(key, emitter);
        log.info("Created emitter for key: {}, Current emitters map: {}", key, emitters.keySet());

        // 초기 연결 확인용 이벤트
        // 아무 데이터도 넣지 않으면 503 Service Unavailable 에러가 발생
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
            emitter.complete();
        });
        emitter.onError(e -> {
            if (emitters.get(key) == emitter) {
                emitters.remove(key);
                log.error("Emitter error for key: {}", key, e);
            }
            emitter.completeWithError(e);
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
                // emitter가 이미 완료되었는지 확인
                if (emitter != null) {
                    log.debug("Sending heartbeat for key: {}", key);
                    emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
                }
            } catch (IOException e) {
                log.info("Client disconnected for key: {}. Cleaning up emitter.", key);
                if (emitter != null) {
                    try {
                        emitter.complete();
                    } catch (IllegalStateException ise) {
                        // 이미 완료된 경우 무시
                        log.debug("Emitter already completed for key: {}", key);
                    }
                    emitters.remove(key);
                }
            } catch (IllegalStateException e) {
                // 이미 완료된 emitter에 대한 예외 처리
                log.debug("Heartbeat skipped for already completed emitter: {}", key);
                emitters.remove(key);
            }
        });
    }

    public void sendNotification(Long memberId, Notification notification) {
        String key = getEmitterKey(memberId);
        SseEmitter emitter = emitters.get(key);
        if (emitter != null) {
            try {
                NotificationDto dto = toDto(notification);
                emitter.send(SseEmitter.event().name("notification").data(dto));
                log.info("{}번 회원에게 알림번호 {} 전송", memberId, notification.getNotificationId());
            } catch (IOException e) {
                log.error("Failed to send SSE notification for memberId: {}", memberId, e);
                emitters.remove(key);
                throw new SseException(ErrorCode.NOTIFICATION_SEND_FAILED, "Notification send failed");
            }
        }
    }

    private String getEmitterKey(Long memberId) {
        return String.valueOf(memberId);
    }

    // 클라이언트에서 로그아웃 시 SSE종료된 이후 emitter 제어하는게 나을 것 같아서 추가
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