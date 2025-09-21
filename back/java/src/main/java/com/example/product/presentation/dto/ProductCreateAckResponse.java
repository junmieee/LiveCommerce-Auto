package com.example.product.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateAckResponse {
  private boolean success;
  private String message;
  private Long product_id;
}
