package com.example.product.presentation;

import com.example.common.api.ApiExampleConstants;
import com.example.product.application.ProductApplicationService;
import com.example.product.presentation.dto.ProductListResponse;
import com.example.product.presentation.dto.ProductResponse;
import com.example.user.presentation.dto.response.SimpleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products - Public")
public class ProductPublicController {

  private final ProductApplicationService service;

  public ProductPublicController(ProductApplicationService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "공개 상품 목록", description = "isActive=true 상품만 반환")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "요청한 조건의 공개 상품 목록",
        content = @Content(schema = @Schema(implementation = ProductListResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 파라미터",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "InvalidRequest",
                        value = ApiExampleConstants.ERROR_GENERIC))),
    @ApiResponse(
        responseCode = "404",
        description = "검색 조건에 맞는 상품 없음",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "NotFound",
                        value = ApiExampleConstants.ERROR_NOT_FOUND_PRODUCT)))
  })
  public ResponseEntity<ProductListResponse> list(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "created_at_desc") String sort) {
    return ResponseEntity.ok(service.publicList(page, limit, search, sort));
  }

  @GetMapping("/{id}")
  @Operation(summary = "공개 상품 상세")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "상품 상세",
        content = @Content(schema = @Schema(implementation = ProductResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 상품 ID",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(name = "InvalidId", value = ApiExampleConstants.ERROR_GENERIC))),
    @ApiResponse(
        responseCode = "404",
        description = "상품을 찾을 수 없음",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "NotFound",
                        value = ApiExampleConstants.ERROR_NOT_FOUND_PRODUCT)))
  })
  public ResponseEntity<ProductResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(service.publicGet(id));
  }
}
