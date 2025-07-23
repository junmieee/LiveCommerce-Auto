package com.example.entity;

import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String paymentMethod;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private String paymentStatus;
    private Long transactionId;
}