package com.example.seller.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SellerRegistrationRequest {
  @Email @NotBlank private String email;
  @NotBlank private String name;

  @Size(min = 8, max = 64)
  @NotBlank
  private String password;

  @NotBlank private String companyName;
  @NotBlank private String businessNumber;
  @Email private String contactEmail;

  @Size(max = 30)
  private String contactPhone;

  private String settlementCycle;
  private Short payoutDay;
}
