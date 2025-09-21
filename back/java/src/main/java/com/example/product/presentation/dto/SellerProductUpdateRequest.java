package com.example.product.presentation.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class SellerProductUpdateRequest {
  private String name;
  private String description;
  private BigDecimal price;
  private Integer stockQuantity;
  private String imageUrl; // placeholder only
  private Boolean isActive;
}
