package com.choongang.auction.streamingauction.domain.member.dto.response;


import com.choongang.auction.streamingauction.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MemberSearchResponse {

    private String username;        // 사용자 계정명
    private String name;           // 실제 이름

    public static MemberSearchResponse of(
            Member member,
            List<String> commonFollowers
    ) {
        return MemberSearchResponse.builder()
                .username(member.getUsername())
                .name(member.getName())
                .build();
    }
}
