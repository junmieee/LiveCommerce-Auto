package com.example.product.presentation.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {
  private boolean success;
  private List<ProductResponse> data;
  private long total;
  private int page;
  private int limit;
}
