package com.choongang.auction.streamingauction.domain.dto.responseDto;

import com.choongang.auction.streamingauction.domain.entity.Chat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatResponseDto(
        String userName,
        String message,
        String nickName,

        // 백엔드에서 LocalDateTime을 제대로 처리할 수 있도록 'yyyy-MM-dd HH:mm:ss.SSS' 형식으로 처리
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        LocalDateTime sentAt
) {
    public static ChatResponseDto fromEntity (Chat chat) {
        return ChatResponseDto.builder()
                .userName(chat.getMember().getUsername())
                .message(chat.getMessage())
                .nickName(chat.getMember().getName())
                .sentAt(chat.getSentAt())
                .build();
    }
}
