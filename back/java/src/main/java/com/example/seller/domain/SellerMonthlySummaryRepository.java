package com.example.seller.domain;

import java.util.List;
import java.util.Optional;

public interface SellerMonthlySummaryRepository {
  List<SellerMonthlySummary> findAll();

  Optional<SellerMonthlySummary> findById(Long id);

  SellerMonthlySummary save(SellerMonthlySummary summary);

  void deleteById(Long id);
}
