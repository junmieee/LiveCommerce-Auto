package com.example.product.service;

import com.example.product.dto.*;
import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
  private final ProductRepository repository;

  public ProductService(ProductRepository repository) {
    this.repository = repository;
  }

  // Public list/detail (active only)
  public ProductListResponse publicList(int page, int limit, String search, String sort) {
    if (page < 1) page = 1;
    if (limit < 1) limit = 10;
    var pageable = org.springframework.data.domain.PageRequest.of(page - 1, limit, parseSort(sort));
    org.springframework.data.domain.Page<Product> pg;
    if (search != null && !search.isBlank()) {
      pg = repository.findByIsActiveTrueAndNameContainingIgnoreCase(search.trim(), pageable);
    } else {
      pg = repository.findByIsActiveTrue(pageable);
    }
    var list =
        pg.getContent().stream()
            .map(ProductService::toDto)
            .collect(java.util.stream.Collectors.toList());
    return new ProductListResponse(true, list, pg.getTotalElements(), page, limit);
  }

  public ProductResponse publicGet(Long id) {
    Product p =
        repository
            .findByIdAndIsActiveTrue(id)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    return toDto(p);
  }

  // Shared
  public ProductResponse get(Long id) {
    Product p =
        repository.findById(id).orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    return toDto(p);
  }

  @Transactional
  public ProductResponse createForSeller(Long sellerId, ProductCreateRequest req) {
    LocalDateTime now = LocalDateTime.now();
    Product p =
        Product.builder()
            .sellerId(sellerId)
            .name(req.getName())
            .description(req.getDescription())
            .price(req.getPrice())
            .stockQuantity(req.getStockQuantity())
            .isActive(req.getIsActive() == null ? Boolean.TRUE : req.getIsActive())
            .createdAt(now)
            .updatedAt(now)
            .build();
    repository.save(p);
    return toDto(p);
  }

  @Transactional
  public ProductResponse update(Long id, ProductUpdateRequest req) {
    Product p =
        repository.findById(id).orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    p.setName(req.getName());
    p.setDescription(req.getDescription());
    p.setPrice(req.getPrice());
    p.setStockQuantity(req.getStockQuantity());
    p.setIsActive(req.getIsActive());
    p.setUpdatedAt(LocalDateTime.now());
    repository.save(p);
    return toDto(p);
  }

  // Seller list with paging/search/sort
  public ProductListResponse sellerList(
      Long sellerId, int page, int limit, String search, String sort) {
    if (page < 1) page = 1;
    if (limit < 1) limit = 10;
    var pageable = org.springframework.data.domain.PageRequest.of(page - 1, limit, parseSort(sort));
    org.springframework.data.domain.Page<Product> pg;
    if (search != null && !search.isBlank()) {
      pg = repository.findBySellerIdAndNameContainingIgnoreCase(sellerId, search.trim(), pageable);
    } else {
      pg = repository.findBySellerId(sellerId, pageable);
    }
    var list =
        pg.getContent().stream()
            .map(ProductService::toDto)
            .collect(java.util.stream.Collectors.toList());
    return new ProductListResponse(true, list, pg.getTotalElements(), page, limit);
  }

  @Transactional
  public void partialUpdate(Long id, SellerProductUpdateRequest req) {
    Product p =
        repository.findById(id).orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    if (req.getName() != null) p.setName(req.getName());
    if (req.getDescription() != null) p.setDescription(req.getDescription());
    if (req.getPrice() != null) p.setPrice(req.getPrice());
    if (req.getStockQuantity() != null) p.setStockQuantity(req.getStockQuantity());
    if (req.getIsActive() != null) p.setIsActive(req.getIsActive());
    p.setUpdatedAt(java.time.LocalDateTime.now());
    repository.save(p);
  }

  @Transactional
  public void toggleActive(Long id, Boolean isActive) {
    Product p =
        repository.findById(id).orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    p.setIsActive(Boolean.TRUE.equals(isActive));
    p.setUpdatedAt(java.time.LocalDateTime.now());
    repository.save(p);
  }

  @Transactional
  public void logicalDelete(Long id) {
    Product p =
        repository.findById(id).orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    p.setIsActive(false);
    p.setUpdatedAt(java.time.LocalDateTime.now());
    repository.save(p);
  }

  private static ProductResponse toDto(Product p) {
    return new ProductResponse(
        p.getId(),
        p.getSellerId(),
        p.getName(),
        p.getDescription(),
        p.getPrice(),
        p.getStockQuantity(),
        p.getIsActive(),
        p.getCreatedAt(),
        p.getUpdatedAt());
  }

  private static org.springframework.data.domain.Sort parseSort(String sort) {
    String s = sort == null ? "" : sort.trim().toLowerCase();
    return switch (s) {
      case "created_at_asc" -> org.springframework.data.domain.Sort.by("createdAt").ascending();
      case "price_asc" -> org.springframework.data.domain.Sort.by("price").ascending();
      case "price_desc" -> org.springframework.data.domain.Sort.by("price").descending();
      default -> org.springframework.data.domain.Sort.by("createdAt").descending();
    };
  }
}
