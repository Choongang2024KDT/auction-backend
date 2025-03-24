package com.choongang.auction.streamingauction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ForbiddenOperationException 처리 (판매자가 아닌 사용자가 경매 종료를 시도하는 경우)
    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<?> handleForbiddenOperation(ForbiddenOperationException ex) {
        // 403 Forbidden 상태 코드와 메시지 반환
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", ex.getMessage()));
    }

    // AuctionNotFoundException 처리 (경매가 존재하지 않는 경우)
    @ExceptionHandler(AuctionNotFoundException.class)
    public ResponseEntity<?> handleAuctionNotFound(AuctionNotFoundException ex) {
        // 404 Not Found 상태 코드와 메시지 반환
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    // 다른 RuntimeException 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        // 500 Internal Server Error 상태 코드와 메시지 반환
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "서버 오류가 발생했습니다. 다시 시도해주세요."));
    }

    // 다른 예외들에 대한 기본 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        // 500 Internal Server Error 상태 코드와 메시지 반환
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "알 수 없는 오류가 발생했습니다."));
    }
}