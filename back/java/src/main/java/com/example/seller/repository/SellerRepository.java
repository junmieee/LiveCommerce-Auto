package com.example.seller.repository;

import com.example.seller.entity.Seller;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
  boolean existsByBusinessNumber(String businessNumber);

  Optional<Seller> findByBusinessNumber(String businessNumber);
}
