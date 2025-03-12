package com.choongang.auction.streamingauction.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");  // 메시지를 받을 URL 패턴
        registry.setApplicationDestinationPrefixes("/auction");  // 메시지를 보내는 URL 패턴
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-connect")  // 웹소켓 연결 엔드포인트
                .setAllowedOrigins("http://localhost:3000") // 명시적으로 React 앱의 URL을 설정
                .withSockJS();  // SockJS 지원 (구형 브라우저 대응)
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        // JSON -> DTO 변환을 위한 메시지 컨버터 추가
        messageConverters.add(new MappingJackson2MessageConverter());

        // true를 반환하여 기본 설정을 사용하도록 함
        return true;
    }
}
