package com.example.seller.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "seller_members")
public class SellerMember {

  @EmbeddedId private SellerMemberId id;

  // OWNER/MANAGER/STAFF 등 문자열로 보관
  private String role;

  @Column(name = "is_default")
  private Boolean isDefault;

  @Column(name = "joined_at")
  private LocalDateTime joinedAt;
}
