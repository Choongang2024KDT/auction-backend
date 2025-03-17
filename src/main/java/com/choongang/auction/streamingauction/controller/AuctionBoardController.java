package com.choongang.auction.streamingauction.controller;

import com.choongang.auction.streamingauction.domain.auctionboard.entity.AuctionBoard;
import com.choongang.auction.streamingauction.domain.product.domain.entity.Product;
import com.choongang.auction.streamingauction.jwt.JwtTokenProvider;
import com.choongang.auction.streamingauction.service.AuctionBoardService;
import com.choongang.auction.streamingauction.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/board")
@Slf4j
@RequiredArgsConstructor
public class AuctionBoardController {

    private final AuctionBoardService auctionBoardService;
    private final ProductService productService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 모든 경매 게시판 조회
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllAuctionBoards() {
        try {
            List<AuctionBoard> auctionBoards = auctionBoardService.findAllAuctionBoards();

            // DTO로 변환하여 순환 참조 방지
            List<Map<String, Object>> boardDtos = auctionBoards.stream()
                    .map(board -> {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("boardId", board.getBoardId());
                        dto.put("name", board.getName());
                        dto.put("content", board.getContent());
                        dto.put("categoryName", board.getCategoryName());
                        dto.put("startPrice", board.getStartPrice());
                        dto.put("bidIncrease", board.getBidIncrease());
                        dto.put("buyNowPrice", board.getBuyNowPrice());
                        dto.put("imageUrl", board.getImageUrl());
                        dto.put("productId", board.getProduct().getProductId());
                        return dto;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok().body(Map.of(
                    "message", "모든 경매 게시판을 조회했습니다.",
                    "auctionBoards", boardDtos,
                    "count", boardDtos.size()
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

            // DTO로 변환
            Map<String, Object> boardDto = new HashMap<>();
            boardDto.put("boardId", auctionBoard.getBoardId());
            boardDto.put("name", auctionBoard.getName());
            boardDto.put("content", auctionBoard.getContent());
            boardDto.put("categoryName", auctionBoard.getCategoryName());
            boardDto.put("startPrice", auctionBoard.getStartPrice());
            boardDto.put("bidIncrease", auctionBoard.getBidIncrease());
            boardDto.put("buyNowPrice", auctionBoard.getBuyNowPrice());
            boardDto.put("imageUrl", auctionBoard.getImageUrl());
            boardDto.put("productId", auctionBoard.getProduct().getProductId());

            return ResponseEntity.ok().body(Map.of(
                    "message", "경매 게시판을 조회했습니다.",
                    "auctionBoard", boardDto
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
            AuctionBoard auctionBoard = auctionBoardService.findAuctionBoardByProduct(product);

            // 상품 DTO 생성
            Map<String, Object> productDto = new HashMap<>();
            productDto.put("productId", product.getProductId());
            productDto.put("name", product.getName());
            productDto.put("description", product.getDescription());
            productDto.put("categoryType", product.getCategory().getCategoryType().name());

            // 경매 게시판 DTO 생성
            Map<String, Object> boardDto = new HashMap<>();
            boardDto.put("boardId", auctionBoard.getBoardId());
            boardDto.put("name", auctionBoard.getName());
            boardDto.put("content", auctionBoard.getContent());
            boardDto.put("categoryName", auctionBoard.getCategoryName());
            boardDto.put("startPrice", auctionBoard.getStartPrice());
            boardDto.put("bidIncrease", auctionBoard.getBidIncrease());
            boardDto.put("buyNowPrice", auctionBoard.getBuyNowPrice());
            boardDto.put("imageUrl", auctionBoard.getImageUrl());

            return ResponseEntity.ok().body(Map.of(
                    "message", "상품 ID로 경매 게시판을 조회했습니다.",
                    "auctionBoard", boardDto,
                    "product", productDto
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
     * 경매 게시판 삭제 (인증된 사용자만 가능)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteAuctionBoard(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 토큰에서 사용자 이름 추출
            String token = authHeader.replace("Bearer ", "");
            String username = jwtTokenProvider.getCurrentLoginUsername(token);

            // 경매 게시판 조회
            AuctionBoard auctionBoard = auctionBoardService.findAuctionBoardById(id);

            // 권한 확인 (관리자 또는 해당 상품 등록자만 삭제 가능)
            Product product = auctionBoard.getProduct();
            if (!product.getMember().getUsername().equals(username) &&
                    !product.getMember().getRole().equals("ROLE_ADMIN")) {
                return ResponseEntity.status(403).body(Map.of(
                        "error", "경매 게시판을 삭제할 권한이 없습니다."
                ));
            }

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

    /**
     * 내 경매 게시판 목록 조회
     */
    @GetMapping("/my-boards")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyAuctionBoards(
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 토큰에서 사용자 이름 추출
            String token = authHeader.replace("Bearer ", "");
            String username = jwtTokenProvider.getCurrentLoginUsername(token);

            // 회원이 등록한 상품 조회
            List<Product> products = productService.findProductsByMember(username);

            // 상품 ID 목록 추출
            List<Long> productIds = products.stream()
                    .map(Product::getProductId)
                    .collect(Collectors.toList());

            // 해당 상품들의 경매 게시판 조회
            List<AuctionBoard> myBoards = auctionBoardService.findAuctionBoardsByProductIds(productIds);

            // DTO로 변환
            List<Map<String, Object>> boardDtos = myBoards.stream()
                    .map(board -> {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("boardId", board.getBoardId());
                        dto.put("name", board.getName());
                        dto.put("content", board.getContent());
                        dto.put("categoryName", board.getCategoryName());
                        dto.put("startPrice", board.getStartPrice());
                        dto.put("bidIncrease", board.getBidIncrease());
                        dto.put("buyNowPrice", board.getBuyNowPrice());
                        dto.put("imageUrl", board.getImageUrl());
                        dto.put("productId", board.getProduct().getProductId());
                        return dto;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok().body(Map.of(
                    "message", "내 경매 게시판 목록을 조회했습니다.",
                    "auctionBoards", boardDtos,
                    "count", boardDtos.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "경매 게시판 목록 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
}