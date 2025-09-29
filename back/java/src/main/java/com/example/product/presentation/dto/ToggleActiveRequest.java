package com.example.product.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ToggleActiveRequest {
  @JsonProperty("product_id")
  private Long productId;

  @JsonProperty("is_active")
  private Boolean isActive;
}
