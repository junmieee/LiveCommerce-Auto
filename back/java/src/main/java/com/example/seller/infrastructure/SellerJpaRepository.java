package com.example.seller.infrastructure;

import com.example.seller.domain.Seller;
import com.example.seller.domain.SellerRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerJpaRepository extends JpaRepository<Seller, Long>, SellerRepository {

  @Override
  boolean existsByBusinessNumber(String businessNumber);

  @Override
  List<Seller> findByIdIn(List<Long> ids);

  @Override
  Optional<Seller> findByBusinessNumber(String businessNumber);
}
