package com.example.seller.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerManagementResponse {
  private boolean success;
  private long totalProducts;
  private long activeProducts;
  private long inactiveProducts;
  private long totalStockQuantity;
  private List<ProductSummary> recentProducts;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ProductSummary {
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean active;
    private LocalDateTime updatedAt;
  }
}
