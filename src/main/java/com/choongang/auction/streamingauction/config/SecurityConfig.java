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

    // 시큐리티 필터체인 빈을 등록
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 커스텀 보안 설정
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                // 세션 인증을 비활성화
                .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 인가 설정
                .authorizeHttpRequests(auth ->
                        auth
                                // 위에부터 검사하므로 구체적인걸 위에
                                // 아래로 갈 수록 덜 구체적인 규칙

                                // 인증이 필요한 요청들
                                .requestMatchers("/api/auth/logout").authenticated()  // 로그아웃은 인증 필요
                                .requestMatchers("/api/auction/**").authenticated()
                                .requestMatchers("/api/chat/**").authenticated()
                                .requestMatchers("/api/notifications/**").authenticated()
                                .requestMatchers("/api/tradeRecord/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/product/{id}").hasRole("ADMIN") // 관리자만 상품 삭제 가능. hasRole() 메서드는 ROLE_ 접두어를 자동으로 추가

                                // 인증 없이 허용할 요청들
                                .requestMatchers("/api/auth/**").permitAll() // 로그아웃 제외 나머지
                                .requestMatchers(HttpMethod.GET, "/api/product/{id}").permitAll()   // 특정 상품 조회
                                .requestMatchers("/api/product/all").permitAll()   // 전체 상품 조회
                                .requestMatchers("/api/product/category/**").permitAll() // 카테고리별 조회
                                .requestMatchers("/api/product/user/**").permitAll() // 특정 회원 상품 조회

                                // 기타 등등 나머지(jsp, css, js, image...)는 모두 허용
                                .anyRequest().permitAll()
                )
                // 토큰을 검사하는 커스텀 인증필터를 시큐리티에 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 시큐리티 기본 인증인가차단의 상태코드는 403으로 지정되어 있음
                // 그런데 403은 인가차단이지 인증차단코드가 아님, 인증차단은 401로 해야 적합함
                .exceptionHandling(ex ->
                    ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )

                // SSE를 위한 비동기 요청 설정
                .requestCache(cache -> cache.disable()) // 캐시 비활성화로 재요청 문제 방지
                .headers(headers -> headers
                        .cacheControl(cache -> cache.disable()) // 캐시 제어 비활성화
                );

        return http.build();
    }
}
