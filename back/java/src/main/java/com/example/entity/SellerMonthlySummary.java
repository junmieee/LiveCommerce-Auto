package com.example.entity;

import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sellermonthlysummarys")
public class SellerMonthlySummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sellerId;
    private String yearMonth;
    private BigDecimal totalSales;
    private BigDecimal totalCommission;
    private LocalDateTime lastUpdatedAt;
}