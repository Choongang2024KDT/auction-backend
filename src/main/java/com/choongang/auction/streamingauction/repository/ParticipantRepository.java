package com.choongang.auction.streamingauction.repository;

import com.choongang.auction.streamingauction.domain.participant.entity.Participant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    // 상품ID로 모든 참가자 조회
    @Query("SELECT p FROM Participant p JOIN FETCH p.member WHERE p.product.productId = :productId")
    List<Participant> findAllByProductId(@Param("productId") Long productId);

    // 특정 회원이 특정 상품에 예약했는지 확인
    Optional<Participant> findByProduct_ProductIdAndMember_Id(Long productId, Long memberId);

    // 특정 회원이 특정 상품에 특정 상태로 예약했는지 확인
    Optional<Participant> findByProduct_ProductIdAndMember_IdAndStatus(
            Long productId, Long memberId, Participant.ParticipantStatus status);

    // 특정 회원이 특정 상품에 여러 상태 중 하나로 예약했는지 확인
    @Query("SELECT p FROM Participant p WHERE p.product.productId = :productId AND p.member.id = :memberId AND p.status IN :statuses")
    Optional<Participant> findByProduct_ProductIdAndMember_IdAndStatusIn(
            @Param("productId") Long productId,
            @Param("memberId") Long memberId,
            @Param("statuses") List<Participant.ParticipantStatus> statuses);

    // 회원의 모든 예약 상품 조회
    @Query("SELECT p FROM Participant p JOIN FETCH p.product WHERE p.member.id = :memberId")
    List<Participant> findAllByMemberId(@Param("memberId") Long memberId);

    // 회원의 사용자명으로 모든 예약 상품 조회
    @Query("SELECT p FROM Participant p JOIN FETCH p.product WHERE p.member.username = :username")
    List<Participant> findAllByMemberUsername(@Param("username") String username);

    // 상품별 참가자 수 카운트
    long countByProduct_ProductId(Long productId);

    // 상품별 특정 상태의 참가자 카운트
    long countByProduct_ProductIdAndStatus(Long productId, Participant.ParticipantStatus status);

    // 상품별 여러 상태의 참가자 카운트
    @Query("SELECT COUNT(p) FROM Participant p WHERE p.product.productId = :productId AND p.status IN :statuses")
    long countByProduct_ProductIdAndStatusIn(
            @Param("productId") Long productId,
            @Param("statuses") List<Participant.ParticipantStatus> statuses);

    // 상태별 참가자 조회
    List<Participant> findByProduct_ProductIdAndStatus(Long productId, Participant.ParticipantStatus status);

    // 참가자 수에 따라 제품을 정렬
    @Query("SELECT p.product.productId as productId, COUNT(p) as participantCount " +
            "FROM Participant p " +
            "WHERE p.status IN :statuses " +
            "GROUP BY p.product.productId " +
            "ORDER BY participantCount DESC")
    List<Object[]> findTopProductsByParticipantCount(
            @Param("statuses") List<Participant.ParticipantStatus> statuses,
            Pageable pageable);
}