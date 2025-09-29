package com.example.seller.application;

import com.example.seller.domain.SellerMonthlySummary;
import com.example.seller.domain.SellerMonthlySummaryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SellerMonthlySummaryService {
  private final SellerMonthlySummaryRepository repository;

  public SellerMonthlySummaryService(SellerMonthlySummaryRepository repository) {
    this.repository = repository;
  }

  public List<SellerMonthlySummary> findAll() {
    return repository.findAll();
  }

  public SellerMonthlySummary save(SellerMonthlySummary item) {
    return repository.save(item);
  }

  public SellerMonthlySummary findById(Long id) {
    return repository.findById(id).orElse(null);
  }

  public void deleteById(Long id) {
    repository.deleteById(id);
  }
}
