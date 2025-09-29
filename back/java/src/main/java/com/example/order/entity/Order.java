package com.example.order.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;
  private BigDecimal totalPrice;
  private String orderStatus;

  private LocalDateTime orderedAt;
  private LocalDateTime confirmedAt;
  private LocalDateTime cancelledAt;
}
