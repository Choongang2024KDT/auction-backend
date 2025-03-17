package com.choongang.auction.streamingauction.repository;


import com.choongang.auction.streamingauction.domain.member.entity.Member;
import com.choongang.auction.streamingauction.repository.custom.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//@Mapper
public interface MemberRepository
        extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    // 회원 정보 생성 (save로 대체)
//    void insert(Member member);

    // 중복 체크용 조회 메서드
    Optional<Member> findByEmail(String email);
    Optional<Member> findByUsername(String username);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    // 사용자명 존재 여부 확인
    boolean existsByUsername(String username);


}
