package com.example.seller.domain;

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
@Table(name = "seller_monthly_summary")
public class SellerMonthlySummary {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "seller_id")
  private Long sellerId;

  private String yearMonth;
  private BigDecimal totalSales;
  private BigDecimal totalCommission;
  private LocalDateTime lastUpdatedAt;
}
