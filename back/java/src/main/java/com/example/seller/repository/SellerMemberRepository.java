package com.example.seller.repository;

import com.example.seller.entity.SellerMember;
import com.example.seller.entity.SellerMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerMemberRepository extends JpaRepository<SellerMember, SellerMemberId> {
  boolean existsByIdSellerIdAndIdUserId(Long sellerId, Long userId);

  boolean existsByIdUserId(Long userId);
}
