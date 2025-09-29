package com.example.product.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
  private Long id;
  private Long sellerId;
  private String name;
  private String descriptionHtml;
  private BigDecimal price;
  private Integer stockQuantity;
  private String thumbnailImageKey;
  private List<String> mainImageKeys;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
