package com.example.seller.domain;

public interface SellerMembershipRepository {

  boolean existsBySellerIdAndUserId(Long sellerId, Long userId);

  boolean existsByUserId(Long userId);

  SellerMember save(SellerMember member);
}
