package com.choongang.auction.streamingauction.jwt;

import com.choongang.auction.streamingauction.jwt.entity.TokenUserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

// 토큰 검증만 수행 - 토큰이 없거나 위조되거나 만료되었으면 요청을 돌려보냄
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;


    // 실제 필터링 로직을 수행하는 메서드
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 사용자가 전달한 토큰을 가져와야 함.
        String token;

        // 임시 코드
        String requestURI = request.getRequestURI();

        // 로그인/로그아웃은 쿠키에서 토큰 가져오기
        if (requestURI.startsWith("/api/auth/login") || requestURI.startsWith("/api/auth/logout")) {
            token = resolveTokenFromCookie(request);
            log.info("token from cookie: {}", token);
        } else {
            token = resolveTokenFromHeader(request);
            log.info("API token from header: {}", token);
        }


        // 기존 코드
//        if (isApiRequest(request)) {
//            // 요청헤더에 토큰을 들고다니는 경우 - API요청
//            log.info("요청헤더에 토큰을 들고다니는 경우 - API요청");
//            token = resolveTokenFromHeader(request);
//        } else {
//            // 쿠키에 토큰을 들고다니는 경우 - 라우팅 요청
//            log.info("쿠키에 토큰을 들고다니는 경우 - 라우팅 요청");
//            token = resolveTokenFromCookie(request);
//        }


        // 토큰 유효성 검증 및 토큰이 유효하다면 스프링에게 유효하다는 정보를 전달
        validateAndAuthenticate(token);


        // 다음 필터로 넘어가기
        filterChain.doFilter(request, response);

    }

    /**
     * 토큰이 유효하면, SecurityContext에 Authentication을 세팅하는 메서드.
     *
     * @param token JWT 토큰 문자열
     */
    private void validateAndAuthenticate(String token) {
        log.debug("parsed token: {}", token);

        // 토큰이 존재하고, 유효성 검증에 통과하면 인증 처리
        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            // 토큰에서 사용자 정보(username과 memberId) 추출
            String username = tokenProvider.getCurrentLoginUsername(token);
            Long memberId = tokenProvider.getCurrentMemberId(token);
            log.info("getCurrentMemberId: {}", memberId.toString());

            // TokenUserInfo 객체 생성
            TokenUserInfo tokenUserInfo = TokenUserInfo.builder()
                    .userName(username)
                    .memberId(memberId)
                    .build();
            log.info("tokenUserInfo: {} {}", tokenUserInfo.userName(), tokenUserInfo.memberId());

            // Spring Security에게 접근을 허용하라고 명령
            // Authentication 객체 생성 → SecurityContextHolder에 저장
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    tokenUserInfo, // 인증된 사용자 정보 객체
                    null,
                    new ArrayList<>()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("authentication success: user - {}, memberId - {}", username, memberId);
        }
    }




    /**
     * API 요청(헤더 기반)에서 토큰을 추출하는 메서드입니다.
     *
     * @param request HttpServletRequest
     * @return Bearer 토큰 문자열(앞의 "Bearer " 제거 후 반환)
     */
    private String resolveTokenFromHeader(HttpServletRequest request) {
        // Authorization 헤더에서 토큰 추출
        String bearerToken = request.getHeader("Authorization");
        // bearerToken이 "Bearer "로 시작하면 해당 부분을 제거하고 토큰만 반환
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        // 조건을 만족하지 못하면 null 반환
        return null;
    }

    private String resolveTokenFromCookie(HttpServletRequest request) {
        // 2. 페이지 요청: 쿠키 체크
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> "accessToken".equals(c.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }

        log.info("resolveTokenFromCookie: no cookie found");
        return null;
    }

    // 현재 요청이 API요청인지 JSP페이지 라우팅 요청인지를 확인
    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/");
    }


}
