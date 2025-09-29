package com.example.seller.infrastructure;

import com.example.seller.domain.SellerMember;
import com.example.seller.domain.SellerMemberId;
import com.example.seller.domain.SellerMembershipChecker;
import com.example.seller.domain.SellerMembershipRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerMemberJpaRepository
    extends JpaRepository<SellerMember, SellerMemberId>,
        SellerMembershipRepository,
        SellerMembershipChecker {

  boolean existsByIdSellerIdAndIdUserId(Long sellerId, Long userId);

  boolean existsByIdUserId(Long userId);

  @Override
  default boolean existsBySellerIdAndUserId(Long sellerId, Long userId) {
    return existsByIdSellerIdAndIdUserId(sellerId, userId);
  }

  @Override
  default boolean existsByUserId(Long userId) {
    return existsByIdUserId(userId);
  }

  @Override
  default boolean isMemberOfSeller(Long sellerId, Long userId) {
    return existsBySellerIdAndUserId(sellerId, userId);
  }
}
