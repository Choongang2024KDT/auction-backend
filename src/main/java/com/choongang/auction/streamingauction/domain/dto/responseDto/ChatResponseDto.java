package com.choongang.auction.streamingauction.domain.dto.responseDto;

import com.choongang.auction.streamingauction.domain.entity.Chat;
import lombok.Builder;

@Builder
public record ChatResponseDto(
        Long userId,
        String message
) {
    public static ChatResponseDto fromEntity (Chat chat) {
        return ChatResponseDto.builder()
                .userId(chat.getId())
                .message(chat.getMessage())
                .build();
    }
}
