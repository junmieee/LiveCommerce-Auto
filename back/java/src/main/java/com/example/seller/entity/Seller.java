package com.example.seller.entity;

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
@Table(name = "sellers")
public class Seller {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(name = "business_number", unique = true)
  private String businessNumber;

  @Column(name = "contact_email")
  private String contactEmail;

  @Column(name = "contact_phone")
  private String contactPhone;

  // PENDING/ACTIVE/SUSPENDED
  private String status;

  @Column(name = "commission_rate")
  private BigDecimal commissionRate;

  @Column(name = "settlement_cycle")
  private String settlementCycle;

  @Column(name = "payout_day")
  private Short payoutDay;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;
}
