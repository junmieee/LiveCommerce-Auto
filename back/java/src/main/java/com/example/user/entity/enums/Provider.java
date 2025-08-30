// src/main/java/com/example/user/entity/enums/Provider.java

package com.example.user.entity.enums;

public enum Provider {
  LOCAL,
  GOOGLE,
  KAKAO;

  public static Provider from(String value) {
    return switch (value.toLowerCase()) {
      case "local" -> LOCAL;
      case "google" -> GOOGLE;
      case "kakao" -> KAKAO;
      default -> throw new IllegalArgumentException("Invalid provider: " + value);
    };
  }
}
