package com.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private final Key key;
  private final long accessTokenValidityMs;
  private final long refreshTokenValidityMs;

  public JwtTokenProvider(
      @Value("${jwt.secret:changeme}") String secret,
      @Value("${jwt.access-validity-ms:900000}") long accessTokenValidityMs, // 15m
      @Value("${jwt.refresh-validity-ms:1209600000}") long refreshTokenValidityMs // 14d
      ) {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    this.key = Keys.hmacShaKeyFor(keyBytes);
    this.accessTokenValidityMs = accessTokenValidityMs;
    this.refreshTokenValidityMs = refreshTokenValidityMs;
  }

  public String generateAccessToken(Long userId, String email) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + accessTokenValidityMs);
    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .claim("email", email)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateRefreshToken(Long userId) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + refreshTokenValidityMs);
    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims parseClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }
}
