package com.example.user.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
