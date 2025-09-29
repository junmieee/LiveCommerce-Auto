package com.example.security;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenStore {

  private final StringRedisTemplate redis;
  private final long refreshTtlMs;

  public RefreshTokenStore(
      StringRedisTemplate redis,
      @Value("${jwt.refresh-validity-ms:1209600000}") long refreshTtlMs) {
    this.redis = redis;
    this.refreshTtlMs = refreshTtlMs;
  }

  private String key(Long userId) {
    return "refresh:" + userId;
  }

  public void store(Long userId, String refreshToken) {
    redis.opsForValue().set(key(userId), refreshToken, Duration.ofMillis(refreshTtlMs));
  }

  public String get(Long userId) {
    return redis.opsForValue().get(key(userId));
  }

  public void delete(Long userId) {
    redis.delete(key(userId));
  }
}
