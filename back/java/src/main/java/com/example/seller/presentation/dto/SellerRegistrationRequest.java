package com.example.seller.presentation.dto;

import lombok.Data;

@Data
public class SellerRegistrationRequest {
  private String companyName;
  private String businessNumber;
  private String contactEmail;
  private String contactPhone;
  private String settlementCycle;
  private Short payoutDay;
}
