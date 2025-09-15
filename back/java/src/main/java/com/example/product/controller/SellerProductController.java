package com.example.product.controller;

import com.example.product.dto.*;
import com.example.product.service.ProductService;
import com.example.security.AuthUser;
import com.example.user.dto.response.SimpleResponse;
import io.swagger.v3.oas.annotations.Operation;
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

  private final ProductService service;

  public SellerProductController(ProductService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "판매자 상품 목록")
  public ResponseEntity<ProductListResponse> list(
      Authentication auth,
      @RequestParam Long sellerId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "created_at_desc") String sort) {
    Long userId = ((AuthUser) auth.getPrincipal()).getId();
    return ResponseEntity.ok(service.sellerList(sellerId, page, limit, search, sort, userId));
  }

  @GetMapping("/{id}")
  @Operation(summary = "판매자 상품 상세")
  public ResponseEntity<ProductResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(service.get(id));
  }

  @PostMapping
  @Operation(summary = "상품 등록")
  public ResponseEntity<ProductCreateAckResponse> create(
      Authentication auth, @RequestParam Long sellerId, @RequestBody ProductCreateRequest req) {
    Long userId = ((AuthUser) auth.getPrincipal()).getId();
    Long id = service.createForSeller(sellerId, req, userId).getId();
    return ResponseEntity.status(201).body(new ProductCreateAckResponse(true, "상품이 등록되었습니다.", id));
  }

  @PatchMapping("/{id}")
  @Operation(summary = "상품 수정")
  public ResponseEntity<SimpleResponse> update(
      Authentication auth, @PathVariable Long id, @RequestBody SellerProductUpdateRequest req) {
    Long userId = ((AuthUser) auth.getPrincipal()).getId();
    service.partialUpdate(id, req, userId);
    return ResponseEntity.ok(new SimpleResponse(true, "상품 정보가 수정되었습니다."));
  }

  @PatchMapping("/toggle-active")
  @Operation(summary = "상품 활성/비활성 토글")
  public ResponseEntity<SimpleResponse> toggleActive(
      Authentication auth, @RequestBody ToggleActiveRequest req) {
    Long userId = ((AuthUser) auth.getPrincipal()).getId();
    service.toggleActive(req.getProductId(), req.getIsActive(), userId);
    return ResponseEntity.ok(new SimpleResponse(true, "상품 상태가 변경되었습니다."));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "상품 삭제(논리)")
  public ResponseEntity<SimpleResponse> delete(Authentication auth, @PathVariable Long id) {
    Long userId = ((AuthUser) auth.getPrincipal()).getId();
    service.logicalDelete(id, userId);
    return ResponseEntity.ok(new SimpleResponse(true, "상품이 삭제되었습니다."));
  }
}
