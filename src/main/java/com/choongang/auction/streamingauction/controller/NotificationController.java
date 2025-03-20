package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.dto.responseDto.NotificationDto;
import com.choongang.auction.streamingauction.domain.entity.Notification;
import com.choongang.auction.streamingauction.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> findAll(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.findAll(userId));
    }

    @PostMapping("/send/{userId}")
    public ResponseEntity<?> sendNotification(@PathVariable Long userId, @RequestBody NotificationDto dto) {
        notificationService.sendNotification(userId, dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read/{id}")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);

        return ResponseEntity.ok().body(Map.of(
                "message", "읽음 처리 되었습니다. id -" +id
        ));
    }
}
