package com.choongang.auction.streamingauction.config;

import com.choongang.auction.streamingauction.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity  // 커스텀 시큐리티 설정파일이라는 의미
@RequiredArgsConstructor
public class SecurityConfig {

    // 커스텀 필터 의존
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // CORS 설정을 가져올 CorsConfig 클래스의 빈을 주입받음
    private final CorsConfig corsConfig;

    // 시큐리티 필터체인 빈을 등록
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 커스텀 보안 설정
        http
                .csrf(csrf -> csrf.disable())
                // CORS 설정을 적용
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource())) // CORS 설정을 여기에 적용
                // 세션 인증을 비활성화
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 인가 설정
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/api/auction/**").authenticated()
                                .requestMatchers("/api/chat/**").authenticated()
                                .requestMatchers("/api/notifications/**").authenticated()
                                .requestMatchers("/api/tradeRecord/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/product/{id}").hasRole("ADMIN") // 관리자만 상품 삭제 가능.
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/product/{id}").permitAll()
                                .requestMatchers("/api/product/all").permitAll()
                                .requestMatchers("/api/product/category/**").permitAll()
                                .requestMatchers("/api/product/user/**").permitAll()
                                .anyRequest().permitAll()
                )
                // JWT 인증 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                );

        return http.build();
    }
}
