package com.example.product.presentation.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductUpdateRequest {
  @NotBlank private String name;
  private String description;

  @NotNull
  @DecimalMin(value = "0.0", inclusive = true)
  private BigDecimal price;

  @NotNull
  @Min(0)
  private Integer stockQuantity;

  @NotNull private Boolean isActive;
}
