package com.example.product.presentation.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductCreateRequest {
  // sellerId는 인증 연동 전까지 query param으로 받습니다.

  @NotBlank private String name;
  private String description;

  @NotNull
  @DecimalMin(value = "0.0", inclusive = true)
  private BigDecimal price;

  @NotNull
  @Min(0)
  private Integer stockQuantity;

  private Boolean isActive = true;

  // image_url은 보류(별도 이미지 테이블 예정)
}
