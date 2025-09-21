package com.example.seller.infrastructure;

import com.example.seller.domain.SellerMonthlySummary;
import com.example.seller.domain.SellerMonthlySummaryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerMonthlySummaryJpaRepository
    extends JpaRepository<SellerMonthlySummary, Long>, SellerMonthlySummaryRepository {}
