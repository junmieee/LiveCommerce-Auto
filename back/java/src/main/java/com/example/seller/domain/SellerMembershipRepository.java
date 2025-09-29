package com.example.seller.domain;

import java.util.List;

public interface SellerMembershipRepository {

  boolean existsBySellerIdAndUserId(Long sellerId, Long userId);

  boolean existsByUserId(Long userId);

  List<SellerMember> findByUserId(Long userId);

  SellerMember save(SellerMember member);
}
