package com.example.product.presentation.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class ProductUpdateRequest {
  @NotBlank private String name;
  private String descriptionHtml;

  @NotNull
  @DecimalMin(value = "0.0", inclusive = true)
  private BigDecimal price;

  @NotNull
  @Min(0)
  private Integer stockQuantity;

  @NotNull private Boolean isActive;

  private String thumbnailImageKey;

  private List<String> mainImageKeys;
}
