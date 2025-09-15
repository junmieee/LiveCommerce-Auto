package com.example.product.controller;

import com.example.product.dto.ProductListResponse;
import com.example.product.dto.ProductResponse;
import com.example.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products - Public")
public class ProductPublicController {

  private final ProductService service;

  public ProductPublicController(ProductService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "공개 상품 목록", description = "isActive=true 상품만 반환")
  public ResponseEntity<ProductListResponse> list(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "created_at_desc") String sort) {
    return ResponseEntity.ok(service.publicList(page, limit, search, sort));
  }

  @GetMapping("/{id}")
  @Operation(summary = "공개 상품 상세")
  public ResponseEntity<ProductResponse> get(@PathVariable Long id) {
    return ResponseEntity.ok(service.publicGet(id));
  }
}
