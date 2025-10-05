package com.example.product.presentation;

import com.example.common.api.ApiExampleConstants;
import com.example.product.application.ProductApplicationService;
import com.example.product.presentation.dto.*;
import com.example.security.SecurityUtils;
import com.example.user.presentation.dto.response.SimpleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller/products")
@Tag(name = "Products - Seller")
@SecurityRequirement(name = "bearerAuth")
public class SellerProductController {

  private final ProductApplicationService service;

  public SellerProductController(ProductApplicationService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "판매자 상품 목록")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "판매자 상품 목록",
        content = @Content(schema = @Schema(implementation = ProductListResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 검색 파라미터",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "InvalidRequest",
                        value = ApiExampleConstants.ERROR_GENERIC))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "Unauthorized",
                        value = ApiExampleConstants.ERROR_UNAUTHORIZED))),
    @ApiResponse(
        responseCode = "403",
        description = "판매자 멤버십 권한 없음",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "Forbidden",
                        value = ApiExampleConstants.ERROR_FORBIDDEN)))
  })
  public ResponseEntity<ProductListResponse> list(
      Authentication auth,
      @RequestParam Long sellerId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "created_at_desc") String sort) {
    Long userId = SecurityUtils.requireAuthUser(auth).getId();
    return ResponseEntity.ok(service.sellerList(sellerId, page, limit, search, sort, userId));
  }

  @GetMapping("/{id}")
  @Operation(summary = "판매자 상품 상세")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "판매자 상품 상세",
        content = @Content(schema = @Schema(implementation = ProductResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "Unauthorized",
                        value = "{\"success\":false,\"message\":\"인증 정보가 필요합니다.\"}"))),
    @ApiResponse(
        responseCode = "403",
        description = "판매자 멤버십 권한 없음",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "Forbidden",
                        value = "{\"success\":false,\"message\":\"해당 판매자에 대한 권한이 없습니다.\"}"))),
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
    return ResponseEntity.ok(service.get(id));
  }

  @PostMapping
  @Operation(summary = "상품 등록")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "상품 등록 성공",
        content = @Content(schema = @Schema(implementation = ProductCreateAckResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "검증 오류",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "ValidationError",
                        value = ApiExampleConstants.ERROR_VALIDATION))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "Unauthorized",
                        value = "{\"success\":false,\"message\":\"인증 정보가 필요합니다.\"}"))),
    @ApiResponse(
        responseCode = "403",
        description = "판매자 멤버십 권한 없음",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "Forbidden",
                        value = "{\"success\":false,\"message\":\"해당 판매자에 대한 권한이 없습니다.\"}")))
  })
  public ResponseEntity<ProductCreateAckResponse> create(
      Authentication auth, @RequestParam Long sellerId, @RequestBody ProductCreateRequest req) {
    Long userId = SecurityUtils.requireAuthUser(auth).getId();
    Long id = service.createForSeller(sellerId, req, userId).getId();
    return ResponseEntity.status(201).body(new ProductCreateAckResponse(true, "상품이 등록되었습니다.", id));
  }

  @PatchMapping("/{id}")
  @Operation(summary = "상품 수정")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "상품 정보 수정 완료",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "UpdateSuccess",
                        value = ApiExampleConstants.SUCCESS_PRODUCT_UPDATE))),
    @ApiResponse(
        responseCode = "400",
        description = "검증 오류",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "ValidationError",
                        value = ApiExampleConstants.ERROR_VALIDATION))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "Unauthorized",
                        value = ApiExampleConstants.ERROR_UNAUTHORIZED))),
    @ApiResponse(
        responseCode = "403",
        description = "판매자 멤버십 권한 없음",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "Forbidden",
                        value = ApiExampleConstants.ERROR_FORBIDDEN))),
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
  public ResponseEntity<SimpleResponse> update(
      Authentication auth, @PathVariable Long id, @RequestBody SellerProductUpdateRequest req) {
    Long userId = SecurityUtils.requireAuthUser(auth).getId();
    service.partialUpdate(id, req, userId);
    return ResponseEntity.ok(new SimpleResponse(true, "상품 정보가 수정되었습니다."));
  }

  @PatchMapping("/toggle-active")
  @Operation(summary = "상품 활성/비활성 토글")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "상품 상태 변경 완료",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "ToggleSuccess",
                        value = ApiExampleConstants.SUCCESS_PRODUCT_TOGGLE))),
    @ApiResponse(
        responseCode = "400",
        description = "검증 오류",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "ValidationError",
                        value = ApiExampleConstants.ERROR_VALIDATION))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "Unauthorized",
                        value = ApiExampleConstants.ERROR_UNAUTHORIZED))),
    @ApiResponse(
        responseCode = "403",
        description = "판매자 멤버십 권한 없음",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "Forbidden",
                        value = ApiExampleConstants.ERROR_FORBIDDEN))),
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
  public ResponseEntity<SimpleResponse> toggleActive(
      Authentication auth, @RequestBody ToggleActiveRequest req) {
    Long userId = SecurityUtils.requireAuthUser(auth).getId();
    service.toggleActive(req.getProductId(), req.getIsActive(), userId);
    return ResponseEntity.ok(new SimpleResponse(true, "상품 상태가 변경되었습니다."));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "상품 삭제(논리)")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "상품 삭제 완료",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "DeleteSuccess",
                        value = ApiExampleConstants.SUCCESS_PRODUCT_DELETE))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "Unauthorized",
                        value = ApiExampleConstants.ERROR_UNAUTHORIZED))),
    @ApiResponse(
        responseCode = "403",
        description = "판매자 멤버십 권한 없음",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "Forbidden",
                        value = ApiExampleConstants.ERROR_FORBIDDEN))),
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
  public ResponseEntity<SimpleResponse> delete(Authentication auth, @PathVariable Long id) {
    Long userId = SecurityUtils.requireAuthUser(auth).getId();
    service.logicalDelete(id, userId);
    return ResponseEntity.ok(new SimpleResponse(true, "상품이 삭제되었습니다."));
  }
}
