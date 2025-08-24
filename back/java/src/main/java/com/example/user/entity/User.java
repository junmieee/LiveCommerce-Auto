package com.example.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String name;
    private Long provider;
    private Long providerId;
    private String profileImage;

    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    private Boolean isActive;
    private Boolean isSeller;

    private String companyName;
    private String businessNumber;
    private String contactEmail;
}