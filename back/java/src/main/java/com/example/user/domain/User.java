package com.example.user.domain;

import com.example.user.domain.enums.Provider;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

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
}
