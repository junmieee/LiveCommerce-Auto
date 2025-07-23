package com.example.entity;

import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "orders")
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