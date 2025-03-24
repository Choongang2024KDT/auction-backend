package com.choongang.auction.streamingauction.service;

import com.choongang.auction.streamingauction.domain.member.entity.Member;
import com.choongang.auction.streamingauction.domain.participant.dto.response.ParticipantDTO;
import com.choongang.auction.streamingauction.domain.participant.entity.Participant;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import com.choongang.auction.streamingauction.repository.MemberRepository;
import com.choongang.auction.streamingauction.repository.ParticipantRepository;
import com.choongang.auction.streamingauction.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    /**
     * 경매 예약 (참가자 등록)
     *
     * @param productId 상품 ID
     * @param username 사용자명
     * @return 참가자 DTO
     */
    public ParticipantDTO reserveAuction(Long productId, String username) {
        log.info("경매 예약 시작: 상품ID={}, 사용자명={}", productId, username);

        // 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다. ID: " + productId));

        // 회원 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다: " + username));

        // 이미 예약했는지 확인
        participantRepository.findByProduct_ProductIdAndMember_Id(productId, member.getId())
                .ifPresent(p -> {
                    throw new RuntimeException("이미 예약한 경매입니다.");
                });

        // 판매자가 자신의 상품을 예약하려는 경우 방지
        if (product.getMember().getId().equals(member.getId())) {
            throw new RuntimeException("자신이 등록한 상품은 예약할 수 없습니다.");
        }

        // 참가자 엔티티 생성 및 저장
        Participant participant = Participant.builder()
                .product(product)
                .member(member)
                .status(Participant.ParticipantStatus.RESERVED)
                .build();

        Participant savedParticipant = participantRepository.save(participant);

        log.info("경매 예약 완료: 참가자ID={}, 상품ID={}, 회원ID={}",
                savedParticipant.getId(), product.getProductId(), member.getId());

        return ParticipantDTO.from(savedParticipant);
    }

    /**
     * 예약 취소
     *
     * @param productId 상품 ID
     * @param username 사용자명
     */
    public void cancelReservation(Long productId, String username) {
        log.info("경매 예약 취소 시작: 상품ID={}, 사용자명={}", productId, username);

        // 회원 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다: " + username));

        // 예약 조회
        Participant participant = participantRepository.findByProduct_ProductIdAndMember_Id(productId, member.getId())
                .orElseThrow(() -> new RuntimeException("예약 정보를 찾을 수 없습니다."));

        // 예약 상태 변경
        participant.setStatus(Participant.ParticipantStatus.CANCELED);
        participantRepository.save(participant);

        log.info("경매 예약 취소 완료: 참가자ID={}, 상품ID={}, 회원ID={}",
                participant.getId(), productId, member.getId());
    }

    /**
     * 상품별 모든 예약자 조회
     *
     * @param productId 상품 ID
     * @return 참가자 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<ParticipantDTO> getParticipantsByProductId(Long productId) {
        log.info("상품별 참가자 조회 시작: 상품ID={}", productId);

        List<Participant> participants = participantRepository.findAllByProductId(productId);

        log.info("상품별 참가자 조회 완료: 상품ID={}, 참가자 수={}", productId, participants.size());

        return participants.stream()
                .map(ParticipantDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * 회원의 모든 예약 조회
     *
     * @param username 사용자명
     * @return 참가자 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<ParticipantDTO> getParticipantsByUsername(String username) {
        log.info("회원별 예약 조회 시작: 사용자명={}", username);

        List<Participant> participants = participantRepository.findAllByMemberUsername(username);

        log.info("회원별 예약 조회 완료: 사용자명={}, 예약 수={}", username, participants.size());

        return participants.stream()
                .map(ParticipantDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * 예약 여부 확인
     *
     * @param productId 상품 ID
     * @param username 사용자명
     * @return 예약 여부
     */
    @Transactional(readOnly = true)
    public boolean isReserved(Long productId, String username) {
        log.info("예약 여부 확인: 상품ID={}, 사용자명={}", productId, username);

        // 회원 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다: " + username));

        return participantRepository.findByProduct_ProductIdAndMember_Id(productId, member.getId())
                .isPresent();
    }

    /**
     * 상품별 참가자 수 조회
     *
     * @param productId 상품 ID
     * @return 참가자 수
     */
    @Transactional(readOnly = true)
    public long countParticipantsByProductId(Long productId) {
        log.info("상품별 참가자 수 조회 시작: 상품ID={}", productId);

        // 예약 상태인 참가자만 카운트
        long count = participantRepository.countByProduct_ProductIdAndStatus(
                productId, Participant.ParticipantStatus.RESERVED);

        log.info("상품별 참가자 수 조회 완료: 상품ID={}, 참가자 수={}", productId, count);

        return count;
    }
}