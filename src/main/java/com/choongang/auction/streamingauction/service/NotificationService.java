package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.notification.dto.response.NotificationDto;
import com.choongang.auction.streamingauction.domain.notification.entity.Notification;
import com.choongang.auction.streamingauction.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SseEmitterService sseEmitterService;

    public List<Notification> findAll(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Notification save(Long userId, String message, String link) {
        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .link(link)
                .build();

        return notificationRepository.save(notification);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new RuntimeException("알림을 찾을 수 없음"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void sendNotification(Long userId, NotificationDto dto) {
        // NotificationDto를 Notification 엔티티로 변환하여 저장
        Notification notification = new Notification(userId, dto.getMessage(), dto.getLink());
        notificationRepository.save(notification); // Notification 엔티티 저장

        // NotificationDto로 변환
        NotificationDto notificationDto = new NotificationDto(
                notification.getMessage(),
                notification.getLink());

        // SSE 푸시 알림 전송
        sseEmitterService.sendNotification(userId, notificationDto);
    }
}
