package com.example.user.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
  private String email;
  private String name;
  private String password; // provider=local일 때만 필요
  private String provider; // local, google, kakao
  private String providerId; // 소셜 로그인 ID
  private Boolean isSeller; // 기본 false
  private String companyName;
  private String businessNumber;
  private String contactEmail;
}
