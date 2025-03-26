package com.choongang.auction.streamingauction.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@ControllerAdvice
@Slf4j
public class SseExceptionHandler {

    @ExceptionHandler(SseException.class)
    public SseEmitter handleSseException(SseException ex, HttpServletRequest request) {
        if (!MediaType.TEXT_EVENT_STREAM_VALUE.equals(request.getHeader("Accept"))) {
            throw ex; // SSE가 아니면 GlobalExceptionHandler로 넘김
        }

        SseEmitter emitter = new SseEmitter();
        try {
            log.info("Handling SSE exception: {}", ex.getMessage());
            emitter.send(SseEmitter.event()
                    .name("error")
                    .data("Error: " + ex.getErrorCode().getMessage()));
        } catch (IOException e) {
            log.error("Failed to send error event: {}", e.getMessage());
            emitter.completeWithError(e);
        }
        return emitter;
    }
}