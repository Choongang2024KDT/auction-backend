package com.choongang.auction.streamingauction.domain.participant.dto.response;

import com.choongang.auction.streamingauction.domain.participant.entity.Participant;

import java.time.LocalDateTime;

public record ParticipantDTO(
        Long id,
        Long productId,
        String productName,
        Long memberId,
        String memberUsername,
        String memberName,
        String memberPhone,
        Participant.ParticipantStatus status
) {
    // 간단한 생성을 위한 정적 팩토리 메서드
    public static ParticipantDTO from(Participant participant) {
        return new ParticipantDTO(
                participant.getId(),
                participant.getProduct().getProductId(),
                participant.getProduct().getName(),
                participant.getMember().getId(),
                participant.getMember().getUsername(),
                participant.getMember().getName(),
                participant.getMember().getPhone(),
                participant.getStatus()
        );
    }
}