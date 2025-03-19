package com.choongang.auction.streamingauction.domain.dto.responseDto;

import com.choongang.auction.streamingauction.domain.entity.Chat;
import lombok.Builder;

@Builder
public record ChatResponseDto(
        String userName,
        String message
) {
    public static ChatResponseDto fromEntity (Chat chat) {
        return ChatResponseDto.builder()
                .userName(chat.getMember().getUsername())
                .message(chat.getMessage())
                .build();
    }
}
