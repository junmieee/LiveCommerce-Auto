package com.example.seller.presentation.dto;

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
public class SellerShippingsResponse {
  private boolean success;
  private int totalShippings;
  private Map<String, Long> statusCounts;
  private List<ShippingSummary> shippings;

  public static SellerShippingsResponse empty() {
    return new SellerShippingsResponse(true, 0, Collections.emptyMap(), Collections.emptyList());
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ShippingSummary {
    private Long shippingId;
    private Long orderId;
    private String shippingStatus;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private String recipientName;
    private String phoneNumber;
    private String trackingNumber;
    private String carrier;
    private String address;
  }
}
