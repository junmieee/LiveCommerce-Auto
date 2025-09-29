package com.example.security;

import org.springframework.security.core.Authentication;

public final class SecurityUtils {

  private SecurityUtils() {}

  public static AuthUser requireAuthUser(Authentication authentication) {
    if (authentication == null || authentication.getPrincipal() == null) {
      throw new IllegalStateException("인증 정보가 없습니다.");
    }
    Object principal = authentication.getPrincipal();
    if (!(principal instanceof AuthUser authUser)) {
      throw new IllegalStateException("인증 정보가 올바르지 않습니다.");
    }
    return authUser;
  }
}
