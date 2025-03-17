package com.choongang.auction.streamingauction.repository.custom;



import com.choongang.auction.streamingauction.domain.member.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    // 추천 사용자 목록 조회 (팔로우하지 않은 사용자 중)
    List<Member> findMembersToSuggest(Long currentUserId, int limit);

    // 검색어 기반 회원 검색 (username 기준)
    List<Member> searchMembers(String keyword);

    // 프로필 이미지 업데이트
    void updateProfileImage(String imageUrl, String username);
}
