package com.example.product.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "seller_id")
  private Long sellerId;

  private String name;
  private String description;
  private BigDecimal price;
  private Integer stockQuantity;

  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
