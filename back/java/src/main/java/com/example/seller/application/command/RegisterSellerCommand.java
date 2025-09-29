package com.example.seller.application.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RegisterSellerCommand {
  Long userId;
  String companyName;
  String businessNumber;
  String contactEmail;
  String contactPhone;
  String settlementCycle;
  Short payoutDay;
}
