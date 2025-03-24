package com.choongang.auction.streamingauction.repository;

import com.choongang.auction.streamingauction.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberIdOrderByCreatedAtDesc(Long memberId);
}
