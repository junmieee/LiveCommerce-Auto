package com.example.user.presentation.dto.response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserProfileResponse {
  private Long id;
  private String email;
  private String name;
  private String profileImage;
  private boolean isActive;
  private boolean isSeller;
  private String companyName;
  private String businessNumber;
  private String contactEmail;
  private LocalDateTime createdAt;
  private LocalDateTime lastLoginAt;
}
