package com.example.product.controller;

import com.example.product.dto.*;
import com.example.product.service.ProductService;
import com.example.user.dto.response.SimpleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller/products")
public class SellerProductController {

  private final ProductService service;

  public SellerProductController(ProductService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<ProductListResponse> list(
      @RequestParam Long sellerId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "created_at_desc") String sort) {
    return ResponseEntity.ok(service.sellerList(sellerId, page, limit, search, sort));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(service.get(id));
  }

  @PostMapping
  public ResponseEntity<ProductCreateAckResponse> create(
      @RequestParam Long sellerId, @RequestBody ProductCreateRequest req) {
    Long id = service.createForSeller(sellerId, req).getId();
    return ResponseEntity.status(201).body(new ProductCreateAckResponse(true, "상품이 등록되었습니다.", id));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<SimpleResponse> update(
      @PathVariable Long id, @RequestBody SellerProductUpdateRequest req) {
    service.partialUpdate(id, req);
    return ResponseEntity.ok(new SimpleResponse(true, "상품 정보가 수정되었습니다."));
  }

  @PatchMapping("/toggle-active")
  public ResponseEntity<SimpleResponse> toggleActive(@RequestBody ToggleActiveRequest req) {
    service.toggleActive(req.getProductId(), req.getIsActive());
    return ResponseEntity.ok(new SimpleResponse(true, "상품 상태가 변경되었습니다."));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<SimpleResponse> delete(@PathVariable Long id) {
    service.logicalDelete(id);
    return ResponseEntity.ok(new SimpleResponse(true, "상품이 삭제되었습니다."));
  }
}
