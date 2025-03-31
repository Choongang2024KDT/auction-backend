package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.dto.responseDto.NotificationDto;
import com.choongang.auction.streamingauction.domain.entity.Notification;
import com.choongang.auction.streamingauction.domain.member.entity.Member;
import com.choongang.auction.streamingauction.repository.MemberRepository;
import com.choongang.auction.streamingauction.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final SseEmitterService sseEmitterService;

    // 특정 유저 알림 전체 조회
    public List<NotificationDto> findAll(Long memberId) {
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long notificationId, Long memberId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림 ID " + notificationId + "를 찾을 수 없습니다."));
        // memberId 검증
        if (!notification.getMember().getId().equals(memberId)) {
            throw new SecurityException("해당 알림에 대한 권한이 없습니다");
        }

        notification.setRead(true);
        log.info("Mark as read: {}", notification.isRead());
    }

    @Transactional
    public void handleAuctionEnd(Long productId, Long sellerId, Long winnerId) {
        Member seller = memberRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("판매자 ID " + sellerId + "를 찾을 수 없습니다."));
        Member winner = memberRepository.findById(winnerId)
                .orElseThrow(() -> new IllegalArgumentException("낙찰자 ID " + winnerId + "를 찾을 수 없습니다."));

        // 안심번호 더미데이터
        String safeNumberForSeller = "050-1234-5678";
        String safeNumberForWinner = "050-5678-1234";

        Notification sellerNotification = createNotification(
                seller, "상품 #" + productId + "이 낙찰되었습니다. 낙찰자와 연락하세요.", "/mypage", safeNumberForSeller
        );
        Notification winnerNotification = createNotification(
                winner, "상품 #" + productId + "을 낙찰받으셨습니다. 판매자와 연락하세요.", "/mypage", safeNumberForWinner
        );

        notificationRepository.saveAll(List.of(sellerNotification, winnerNotification));

        sseEmitterService.sendToEmitter(sellerId, sellerNotification);
        sseEmitterService.sendToEmitter(winnerId, winnerNotification);
    }

    private Notification createNotification(Member member, String message, String link, String safeNumber) {
        return Notification.builder()
                .member(member)
                .message(message)
                .link(link)
                .safeNumber(safeNumber)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
    }

    private NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .notificationId(notification.getNotificationId())
                .message(notification.getMessage())
                .link(notification.getLink())
                .isRead(notification.isRead())
                .safeNumber(notification.getSafeNumber())
                .createdAt(notification.getCreatedAt().toString())
                .build();
    }
}
