package com.now.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT(Json Web Token) 생성 및 검증을 담당하는 서비스 객체
 */
@Service
public class JwtTokenService {

    private static final String BEARER_PREFIX_WITH_SPACE = "Bearer ";
    private static final int MINUS_EXPIRE_HOURS = 1;

    @Value("${now.security.key}")
    private String securityKey;

    /**
     * 전달받은 키-값 쌍을 기반으로 JWT 토큰을 생성 후 반환
     *
     * @param claims 토큰에 담을 클레임 정보를 포함한 Map 객체
     * @return JWT 토큰을 생성 후 반환
     */
    public String create(Map<String, Object> claims) {
        return buildToken(claims);
    }

    /**
     * 전달받은 키와 값으로 JWT 토큰을 생성 후 반환
     *
     * @param key   토큰에 담을 키값
     * @param value 토큰에 담을 값
     * @return 생성된 JWT 토큰
     */
    public String create(String key, String value) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(key, value);
        return buildToken(claims);
    }

    /**
     * 전달받은 토큰을 검증하고, 전달받은 키에 해당하는 클레임 값을 반환
     *
     * @param token 검증할 JWT 토큰
     * @param key   가져올 클레임의 키
     * @return 전달받은 키에 해당하는 클레임 값
     * @throws ExpiredJwtException      토큰이 만료된 경우
     * @throws UnsupportedJwtException  지원되지 않는 형식의 JWT인 경우
     * @throws MalformedJwtException    유효하지 않은 형식의 JWT인 경우
     * @throws SignatureException       JWT 서명 검증에 실패한 경우
     * @throws IllegalArgumentException 잘못된 인자가 전달된 경우
     */
    public Object getClaim(String token, String key) {
        Claims claims = Jwts.parser()
                .setSigningKey(Base64.getEncoder().encodeToString(securityKey.getBytes()))
                .parseClaimsJws(removeBearer(token))
                .getBody();

        return claims.get(key);
    }

    /**
     * 전달받은 토큰을 검증하고, 모든 클레임 값을 Map 형태로 반환
     *
     * @param token 검증할 JWT 토큰
     * @return 모든 클레임 값을 포함하는 Map 객체
     * @throws ExpiredJwtException      토큰이 만료된 경우
     * @throws UnsupportedJwtException  지원되지 않는 형식의 JWT인 경우
     * @throws MalformedJwtException    유효하지 않은 형식의 JWT인 경우
     * @throws SignatureException       JWT 서명 검증에 실패한 경우
     * @throws IllegalArgumentException 잘못된 인자가 전달된 경우
     */
    public Map<String, Object> getAllClaims(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(Base64.getEncoder().encodeToString(securityKey.getBytes()))
                .parseClaimsJws(removeBearer(token))
                .getBody();

        return new HashMap<>(claims);
    }


    /**
     * 전달받은 토큰의 접두사를 붙인 토큰 반환
     *
     * @param token 원본 토큰
     * @return Bearer 접두사가 붙은 토큰
     */
    private String addBearerPrefix(String token) {
        return BEARER_PREFIX_WITH_SPACE + token;
    }

    /**
     * 전달받은 토큰의 접두사를 제거한 토큰 반환
     *
     * @param token Bearer 접두사가 포함된 토큰
     * @return Bearer 접두사가 제거된 토큰
     */
    private String removeBearer(String token) {
        return token.substring(BEARER_PREFIX_WITH_SPACE.length());
    }

    /**
     * 지정된 키-값 쌍을 기반으로 JWT 토큰을 생성 후 반환
     *
     * @param claims 토큰에 담을 클레임 정보를 포함한 Map 객체
     * @return JWT 토큰을 생성 후 반환
     */
    private String buildToken(Map<String, Object> claims) {
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(Date.from(Instant.now().plus(MINUS_EXPIRE_HOURS, ChronoUnit.HOURS)))
                .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(securityKey.getBytes()))
                .compact();

        return addBearerPrefix(token);
    }
}
