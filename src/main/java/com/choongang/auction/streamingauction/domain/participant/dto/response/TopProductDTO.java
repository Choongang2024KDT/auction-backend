package com.choongang.auction.streamingauction.domain.participant.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class TopProductDTO {
    private Long productId;
    private Long participantCount;

    public TopProductDTO(Long productId, Long participantCount) {
        this.productId = productId;
        this.participantCount = participantCount;
    }
}
