package com.example.seller.domain;

import java.util.List;
import java.util.Optional;

public interface SellerRepository {

  boolean existsByBusinessNumber(String businessNumber);

  Seller save(Seller seller);

  Optional<Seller> findById(Long id);

  List<Seller> findByIdIn(List<Long> ids);

  Optional<Seller> findByBusinessNumber(String businessNumber);
}
