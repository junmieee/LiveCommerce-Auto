package com.example.service;

import com.example.entity.SellerMonthlySummary;
import com.example.repository.SellerMonthlySummaryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

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
