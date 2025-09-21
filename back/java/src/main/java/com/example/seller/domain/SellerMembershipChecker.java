package com.example.seller.domain;

public interface SellerMembershipChecker {
  boolean isMemberOfSeller(Long sellerId, Long userId);
}
