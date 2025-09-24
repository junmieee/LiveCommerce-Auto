package com.example.product.presentation.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class SellerProductUpdateRequest {
  private String name;
  private String descriptionHtml;
  private BigDecimal price;
  private Integer stockQuantity;
  private String thumbnailImageKey;
  private List<String> mainImageKeys;
  private Boolean isActive;
}
