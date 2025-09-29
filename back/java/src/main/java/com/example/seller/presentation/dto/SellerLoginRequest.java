package com.example.seller.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SellerLoginRequest {
  @Email @NotBlank private String email;
  @NotBlank private String password;
}
