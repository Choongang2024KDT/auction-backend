package com.choongang.auction.streamingauction.repository;

import com.choongang.auction.streamingauction.domain.entity.Chat;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,Long> {

    //특정 경매에 해당하는 메세지 목록 조회
    @EntityGraph(attributePaths = {"auction"}) // 'auction'을 즉시 로딩 LazyInitializationException을 방지
    List<Chat> findByAuctionIdOrderBySentAtAsc(Long auctionId);
}
