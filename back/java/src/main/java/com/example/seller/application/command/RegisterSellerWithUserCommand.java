package com.example.seller.application.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RegisterSellerWithUserCommand {
  String email;
  String name;
  String password;
  String companyName;
  String businessNumber;
  String contactEmail;
  String contactPhone;
  String settlementCycle;
  Short payoutDay;
}
