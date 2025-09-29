package com.example.seller.presentation.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerLoginResponse {
  private boolean success;
  private String accessToken;
  private String refreshToken;
  private Long userId;
  private List<SellerAccount> sellers;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SellerAccount {
    private Long sellerId;
    private String sellerName;
    private String role;
    private boolean defaultAccount;
    private String status;
  }
}
