package org.boot.reservationproject.global.jwt;

import io.jsonwebtoken.security.Keys;
import jakarta.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.global.redis.RedisDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;
import io.jsonwebtoken.*;
@Component
@Slf4j
public class JwtTokenProvider {
  private final Key key;
  private final RedisDao redisDao;
  private static final int JWT_EXPIRATION_MS = 604800000; // 유효시간 : 일주일
  public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, RedisDao redisDao){
    this.redisDao = redisDao;
    byte[] secretByteKey = DatatypeConverter.parseBase64Binary(secretKey);
    this.key = Keys.hmacShaKeyFor(secretByteKey);
  }

  public TokenDto generateToken(String userEmail, Collection<? extends GrantedAuthority> authorities) {
    String authoritiesString = authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);
    String accessToken= Jwts.builder()
        .setSubject(userEmail) // 사용자
        .claim("auth",authoritiesString)
        .setIssuedAt(new Date()) // 현재 시간 기반으로 생성
        .setExpiration(new Date(now.getTime()+60 * 60 * 1000L)) // 만료 시간 세팅 (60분)
        .signWith(key, SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘, signature에 들어갈 secret 값 세팅
        .compact();
    String refreshToken=Jwts.builder()
        .setSubject(userEmail) // 사용자
        .setIssuedAt(new Date()) // 현재 시간 기반으로 생성
        .setExpiration(expiryDate) // 만료 시간 세팅 .setExpiration(expiryDate)
        .signWith(key, SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘, signature에 들어갈 secret 값 세팅
        .compact();
    // redis에 저장
    redisDao.setValues(userEmail, refreshToken, JWT_EXPIRATION_MS + 5000L);

    return TokenDto.builder()
        .grantType("Bearer")
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  // Jwt 토큰에서 이메일 추출
  public String getUserEmailFromJWT(String token) {
    log.info("JWT 토큰: {}", token); // JWT 토큰 로깅
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();

    return claims.getSubject();
  }

  // Jwt 토큰 유효성 검사
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

}
