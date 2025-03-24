package com.choongang.auction.streamingauction.jwt;


import com.choongang.auction.streamingauction.exception.AuthenticationException;
import com.choongang.auction.streamingauction.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// 인증을 위한 토큰을 생성하여 발급하고
// 전송된 토큰의 위조 및 만료시간을 검사하는 역할
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    // 비밀키를 생성
    private SecretKey key;

    @PostConstruct
    public void init() {
        // Base64로 인코딩된 key를 디코딩 후, HMAC-SHA 알고리즘으로 다시 암호화
        this.key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtProperties.getSecretKey())
        );
    }

    // 토큰 발급 로직
    // 액세스 토큰 생성 메서드 수정
    public String createAccessToken(String username, String name, Long memberId) {
        return createToken(username, name, memberId, jwtProperties.getAccessTokenValidityTime());
    }

    // 리프레시 토큰 생성 메서드 수정
    public String createRefreshToken(String username, String name, Long memberId) {
        return createToken(username, name, memberId, jwtProperties.getRefreshTokenValidityTime());
    }

    // 공통 토큰 생성 로직 수정
    private String createToken(String username, String name, Long memberId, long validityTime) {
        // 현재 시간
        Date now = new Date();
        // 만료시간
        Date validity = new Date(now.getTime() + validityTime);

        // Claims 객체를 생성하여 name 정보 추가
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("name", name); // 사용자 이름 추가
        claims.put("memberId", memberId); // 회원 ID 추가

        // 서명을 넣어야 함
        return Jwts.builder()
                .setClaims(claims) // claims 설정 (subject와 name, memberId 포함)
                .setIssuer("streaming-auction")  // 발급자 정보
                .setIssuedAt(now) // 발급시간
                .setExpiration(validity) // 만료시간
                .signWith(key) // 서명 포함
                .compact();
    }

    /**
     * 토큰이 유효한지 검증하는 메서드
     * @param token JWT 토큰
     * @return 토큰이 정상이면 true, 만료되었거나 위조되었다면 false
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("만료된 토큰: {}", e.getMessage());
            throw new AuthenticationException(ErrorCode.EXPIRED_TOKEN);
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.warn("잘못된 JWT 서명: {}", e.getMessage());
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN, "잘못된 JWT 서명입니다.");
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.warn("잘못된 JWT 토큰: {}", e.getMessage());
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN, "잘못된 형식의 JWT 토큰입니다.");
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("유효하지 않은 토큰: {}", e.getMessage());
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        }
    }
    /**
     * 검증된 토큰에서 사용자이름을 추출하는 메서드
     * @param token - 인증 토큰
     * @return 토큰에서 추출한 사용자 이름
     */
    public String getCurrentLoginUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 검증된 토큰에서 사용자 이름(name)을 추출하는 메서드
     * @param token - 인증 토큰
     * @return 토큰에서 추출한 사용자 이름(name)
     */
    public String getCurrentUserName(String token) {
        return parseClaims(token).get("name", String.class);
    }

    /**
     * 검증된 토큰에서 회원 ID를 추출하는 메서드
     * @param token - 인증 토큰
     * @return 토큰에서 추출한 회원 ID
     */
    public Long getCurrentMemberId(String token) {
        return parseClaims(token).get("memberId", Long.class);
    }

    /**
     * 내부적으로 토큰을 파싱하여 Claims 객체를 반환하는 메서드입니다.
     *
     * @param token JWT 토큰
     * @return 파싱된 Claims 객체
     * @throws JwtException 토큰이 유효하지 않은 경우 발생
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}