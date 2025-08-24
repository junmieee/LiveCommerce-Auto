package com.example.shipping.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shippings")
public class Shipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String recipientName;
    private String address;
    private String phoneNumber;
    private String trackingNumber;
    private String carrier;
    private String shippingStatus;

    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime orderedAt;
}