package com.choongang.auction.streamingauction.service;


import com.choongang.auction.streamingauction.domain.auctionboard.entity.AuctionBoard;
import com.choongang.auction.streamingauction.repository.AuctionBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuctionBoardService {

    private final AuctionBoardRepository auctionBoardRepository;

    // 모든 게시물 조회
    @Transactional(readOnly = true)
    public List<AuctionBoard> findAllAuctionBoards() {
        return auctionBoardRepository.findAll();
    }


    // 상품 id로 게시물 조회
    @Transactional(readOnly = true)
    public AuctionBoard findAuctionBoardById(Long id) {
        return auctionBoardRepository.findById(id).orElseThrow(() -> new RuntimeException("조회된 게시물이 없습니다" ));
    }

    // 게시물 삭제
    public void deleteByAuctionBoard(Long auctionId) {
        log.info("경매 게시물 삭제: ID={}", auctionId);

        // 게시물이 존재하는지 확인
        AuctionBoard auctionBoard = auctionBoardRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("삭제할 게시물이 존재하지 않습니다. ID: " + auctionId));

        // 게시물 삭제
        auctionBoardRepository.deleteById(auctionId);

        log.info("경매 게시물 삭제 완료: ID={}", auctionId);
    }

}
