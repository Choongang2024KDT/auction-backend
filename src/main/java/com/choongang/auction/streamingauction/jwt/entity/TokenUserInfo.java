package com.choongang.auction.streamingauction.jwt.entity;

import lombok.Builder;

@Builder
public record TokenUserInfo(

        Long memberId,
        String userName

) {


}
