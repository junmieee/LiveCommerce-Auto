package com.example.seller.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerOrdersResponse {
  private boolean success;
  private int totalOrders;
  private Map<String, Long> statusCounts;
  private List<OrderSummary> orders;

  public static SellerOrdersResponse empty() {
    return new SellerOrdersResponse(true, 0, Collections.emptyMap(), Collections.emptyList());
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OrderSummary {
    private Long orderId;
    private Long buyerId;
    private BigDecimal totalPrice;
    private String orderStatus;
    private LocalDateTime orderedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
    private List<OrderItemSummary> items;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OrderItemSummary {
    private Long orderItemId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
  }
}
