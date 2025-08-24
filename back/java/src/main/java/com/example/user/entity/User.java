package com.example.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.example.user.entity.enums.Provider;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    private String providerId;
    private String password;
    private String profileImage;

    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    private Boolean isActive;
    private Boolean isSeller;

    private String companyName;
    private String businessNumber;
    private String contactEmail;
}