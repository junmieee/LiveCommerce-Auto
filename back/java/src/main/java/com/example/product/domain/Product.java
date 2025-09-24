package com.example.product.domain;

import com.example.common.jpa.StringListJsonConverter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
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

  @Column(name = "description_html")
  private String descriptionHtml;

  private BigDecimal price;
  private Integer stockQuantity;

  @Column(name = "thumbnail_image_key")
  private String thumbnailImageKey;

  @Builder.Default
  @Column(name = "main_image_keys")
  @Convert(converter = StringListJsonConverter.class)
  private List<String> mainImageKeys = new ArrayList<>();

  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
