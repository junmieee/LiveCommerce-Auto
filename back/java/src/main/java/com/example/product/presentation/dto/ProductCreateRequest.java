package com.example.product.presentation.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class ProductCreateRequest {
  // sellerId는 인증 연동 전까지 query param으로 받습니다.

  @NotBlank private String name;
  private String descriptionHtml;

  @NotNull
  @DecimalMin(value = "0.0", inclusive = true)
  private BigDecimal price;

  @NotNull
  @Min(0)
  private Integer stockQuantity;

  private Boolean isActive = true;

  private String thumbnailImageKey;

  private List<String> mainImageKeys;
}
