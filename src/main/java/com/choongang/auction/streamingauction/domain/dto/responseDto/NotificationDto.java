package com.choongang.auction.streamingauction.domain.dto.responseDto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long notificationId;
    private String message;
    private String link;
    private boolean isRead;
    private String createdAt;
    private String safeNumber;

    // expiresAt 보류
}