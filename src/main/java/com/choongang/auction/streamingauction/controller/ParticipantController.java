package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.participant.dto.response.ApiResponse;
import com.choongang.auction.streamingauction.domain.participant.dto.response.ParticipantDTO;
import com.choongang.auction.streamingauction.jwt.entity.TokenUserInfo;
import com.choongang.auction.streamingauction.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
@Slf4j
public class ParticipantController {

    private final ParticipantService participantService;

    /**
     * 경매 예약하기 API
     * 현재 로그인한 사용자가 상품을 예약
     */
    /**
     * 경매 예약하기 API
     * 현재 로그인한 사용자가 상품을 예약
     */
    @PostMapping("/reserve/{productId}")
    public ResponseEntity<ApiResponse<ParticipantDTO>> reserveAuction(
            @PathVariable Long productId,
            @AuthenticationPrincipal TokenUserInfo userInfo) {

        try {
            log.info("경매 예약 API 호출: 상품ID={}, 사용자명={}, 회원ID={}",
                    productId, userInfo.userName(), userInfo.memberId());
            ParticipantDTO participantDTO = participantService.reserveAuction(productId, userInfo.userName());
            return ResponseEntity.ok(ApiResponse.success("경매가 성공적으로 예약되었습니다.", participantDTO));
        } catch (Exception e) {
            log.error("경매 예약 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 예약 취소하기 API
     * 현재 로그인한 사용자의 예약 취소
     */
    @DeleteMapping("/cancel/{productId}")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(
            @PathVariable Long productId,
            @AuthenticationPrincipal TokenUserInfo userInfo) {  // String username -> TokenUserInfo userInfo

        try {
            log.info("경매 예약 취소 API 호출: 상품ID={}, 사용자={}", productId, userInfo);
            participantService.cancelReservation(productId, userInfo.userName());  // username -> userInfo.userName()
            return ResponseEntity.ok(ApiResponse.success("경매 예약이 취소되었습니다.", null));
        } catch (Exception e) {
            log.error("경매 예약 취소 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-reservations")
    public ResponseEntity<ApiResponse<List<ParticipantDTO>>> getMyReservations(
            @AuthenticationPrincipal TokenUserInfo userInfo) {  // String username -> TokenUserInfo userInfo

        try {
            log.info("내 예약 목록 조회 API 호출: 사용자={}", userInfo);
            List<ParticipantDTO> participants = participantService.getParticipantsByUsername(userInfo.userName());  // username -> userInfo.userName()
            return ResponseEntity.ok(ApiResponse.success(participants));
        } catch (Exception e) {
            log.error("내 예약 목록 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> checkReservation(
            @PathVariable Long productId,
            @AuthenticationPrincipal TokenUserInfo userInfo) {  // String username -> TokenUserInfo userInfo

        try {
            log.info("예약 여부 확인 API 호출: 상품ID={}, 사용자={}", productId, userInfo);
            boolean isReserved = participantService.isReserved(productId, userInfo.userName());  // username -> userInfo.userName()
            return ResponseEntity.ok(ApiResponse.success(isReserved));
        } catch (Exception e) {
            log.error("예약 여부 확인 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    /**
     * 상품의 모든 참가자 조회 API
     * 주로 관리자나 판매자가 사용
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ParticipantDTO>>> getParticipantsByProductId(
            @PathVariable Long productId) {

        try {
            log.info("상품별 참가자 조회 API 호출: 상품ID={}", productId);
            List<ParticipantDTO> participants = participantService.getParticipantsByProductId(productId);
            return ResponseEntity.ok(ApiResponse.success(participants));
        } catch (Exception e) {
            log.error("상품별 참가자 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 상품별 참가자 수만 조회하는 API
     */
    @GetMapping("/count/{productId}")
    public ResponseEntity<ApiResponse<Long>> getParticipantsCountByProductId(
            @PathVariable Long productId) {

        try {
            log.info("상품별 참가자 수 조회 API 호출: 상품ID={}", productId);
            long count = participantService.countParticipantsByProductId(productId);
            return ResponseEntity.ok(ApiResponse.success(count));
        } catch (Exception e) {
            log.error("상품별 참가자 수 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}