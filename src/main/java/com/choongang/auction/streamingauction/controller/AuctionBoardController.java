package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.auctionboard.entity.AuctionBoard;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import com.choongang.auction.streamingauction.service.AuctionBoardService;
import com.choongang.auction.streamingauction.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/board")
@Slf4j
@RequiredArgsConstructor
public class AuctionBoardController {

    private final AuctionBoardService auctionBoardService;
    private final ProductService productService;

    /**
     * 모든 경매 게시판 조회
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllAuctionBoards() {
        try {
            List<AuctionBoard> auctionBoards = auctionBoardService.findAllAuctionBoards();
            return ResponseEntity.ok().body(Map.of(
                    "message", "모든 경매 게시판을 조회했습니다.",
                    "auctionBoards", auctionBoards,
                    "count", auctionBoards.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "경매 게시판 목록 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    /**
     * ID로 경매 게시판 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAuctionBoardById(@PathVariable Long id) {
        try {
            AuctionBoard auctionBoard = auctionBoardService.findAuctionBoardById(id);
            return ResponseEntity.ok().body(Map.of(
                    "message", "경매 게시판을 조회했습니다.",
                    "auctionBoard", auctionBoard
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "경매 게시판 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    /**
     * 상품 ID로 경매 게시판 조회
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getAuctionBoardByProductId(@PathVariable Long productId) {
        try {
            // 먼저 상품 조회
            Product product = productService.findProduct(productId);
            // 해당 상품의 경매 게시판 조회
            AuctionBoard auctionBoard = auctionBoardService.findAuctionBoardById(productId);

            return ResponseEntity.ok().body(Map.of(
                    "message", "상품 ID로 경매 게시판을 조회했습니다.",
                    "auctionBoard", auctionBoard,
                    "product", product
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "경매 게시판 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    /**
     * 경매 게시판 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuctionBoard(@PathVariable Long id) {
        try {
            // 존재하는지 먼저 확인
            auctionBoardService.findAuctionBoardById(id);
            // 삭제 진행
            auctionBoardService.deleteByAuctionBoard(id);

            return ResponseEntity.ok().body(Map.of(
                    "message", "경매 게시판이 삭제되었습니다.",
                    "boardId", id
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "경매 게시판 삭제 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
}