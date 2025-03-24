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
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final SseEmitterService sseEmitterService;

    // 특정 유저 알림 전체 조회
    public List<Notification> findAll(Long memberId) {
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없음"));
        notification.setRead(true);
    }

    @Transactional
    public void handleAuctionEnd(Long productId, Long sellerId, Long winnerId) {
        Member seller = memberRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("판매자를 찾을 수 없음"));
        Member winner = memberRepository.findById(winnerId)
                .orElseThrow(() -> new RuntimeException("낙찰자를 찾을 수 없음"));

        // 안심번호 더미데이터
        String safeNumberForSeller = "050-1234-5678";
        String safeNumberForWinner = "050-5678-1234";

        Notification sellerNotification = Notification.builder()
                .member(seller)
                .message("상품 #" + productId + "이 낙찰되었습니다. 낙찰자와 연락하세요.")
                .link("/mypage")
                .safeNumber(safeNumberForSeller)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        notificationRepository.save(sellerNotification);

        Notification winnerNotification = Notification.builder()
                .member(winner)
                .message("상품 #" + productId + "을 낙찰받으셨습니다. 판매자와 연락하세요.")
                .link("/mypage")
                .safeNumber(safeNumberForWinner)
                .safeNumber(safeNumberForSeller)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        notificationRepository.save(winnerNotification);

        // SSE 전송 (DTO로 변환은 SseEmitterService에서 처리)
        sseEmitterService.sendNotification(sellerId, sellerNotification);
        sseEmitterService.sendNotification(winnerId, winnerNotification);
    }
}
