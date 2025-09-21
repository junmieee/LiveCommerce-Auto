package com.example.seller.domain;

import java.util.Optional;

public interface SellerRepository {

  boolean existsByBusinessNumber(String businessNumber);

  Seller save(Seller seller);

  Optional<Seller> findById(Long id);

  Optional<Seller> findByBusinessNumber(String businessNumber);
}
