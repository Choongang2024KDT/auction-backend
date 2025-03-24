package com.choongang.auction.streamingauction.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;

// API에서 발생한 모든 에러들을 모아서 일괄 처리
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 알 수 없는 기타 등등 에러를 일괄 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception e, HttpServletRequest request) {
        log.error("Unexpected error occurred: {}", e.getMessage(), e);

        // 에러 응답 객체 생성
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                .path(request.getRequestURI())
                .error(ErrorCode.INTERNAL_SERVER_ERROR.name())
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value())
                .build();

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(response);
    }

    // 입력값 검증 예외처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("Validation error occurred: {}", e.getMessage(), e);

        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(e.getStatusCode().value())
                .error(e.getStatusCode().toString())
                .message(errorMessage)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(e.getStatusCode())
                .body(response);
    }

    // 인증 관련 예외처리 (401 Unauthorized)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException e, HttpServletRequest request) {

        log.error("인증 오류 발생: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.name())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    // 회원 관련 예외처리
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorResponse> handleMemberException(
            MemberException e, HttpServletRequest request) {

        log.error("MemberException occurred: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(e.getErrorCode().getStatus().value())
                .error(e.getErrorCode().name())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(response);
    }

    // ForbiddenOperationException 처리 (판매자가 아닌 사용자가 경매 종료를 시도하는 경우)
    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenOperation(
            ForbiddenOperationException e, HttpServletRequest request) {

        log.error("ForbiddenOperationException occurred: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.name())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    // AuctionNotFoundException 처리 (경매가 존재하지 않는 경우)
    @ExceptionHandler(AuctionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuctionNotFound(
            AuctionNotFoundException e, HttpServletRequest request) {

        log.error("AuctionNotFoundException occurred: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.name())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
}