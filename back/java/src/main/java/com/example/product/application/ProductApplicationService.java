package com.example.product.application;

import com.example.product.domain.Product;
import com.example.product.domain.ProductRepository;
import com.example.product.presentation.dto.ProductCreateRequest;
import com.example.product.presentation.dto.ProductListResponse;
import com.example.product.presentation.dto.ProductResponse;
import com.example.product.presentation.dto.ProductUpdateRequest;
import com.example.product.presentation.dto.SellerProductUpdateRequest;
import com.example.seller.domain.SellerMembershipChecker;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductApplicationService {

  private final ProductRepository productRepository;
  private final SellerMembershipChecker sellerMembershipChecker;

  public ProductApplicationService(
      ProductRepository productRepository, SellerMembershipChecker sellerMembershipChecker) {
    this.productRepository = productRepository;
    this.sellerMembershipChecker = sellerMembershipChecker;
  }

  public ProductListResponse publicList(int page, int limit, String search, String sort) {
    if (page < 1) page = 1;
    if (limit < 1) limit = 10;
    PageRequest pageable = PageRequest.of(page - 1, limit, parseSort(sort));
    Page<Product> pg;
    if (search != null && !search.isBlank()) {
      pg = productRepository.findByIsActiveTrueAndNameContainingIgnoreCase(search.trim(), pageable);
    } else {
      pg = productRepository.findByIsActiveTrue(pageable);
    }
    var list =
        pg.getContent().stream().map(ProductApplicationService::toDto).collect(Collectors.toList());
    return new ProductListResponse(true, list, pg.getTotalElements(), page, limit);
  }

  public ProductResponse publicGet(Long id) {
    Product product =
        productRepository
            .findByIdAndIsActiveTrue(id)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    return toDto(product);
  }

  public ProductResponse get(Long id) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    return toDto(product);
  }

  @Transactional
  public ProductResponse createForSeller(Long sellerId, ProductCreateRequest req, Long userId) {
    assertMemberOfSeller(sellerId, userId);
    LocalDateTime now = LocalDateTime.now();
    Product product =
        Product.builder()
            .sellerId(sellerId)
            .name(req.getName())
            .descriptionHtml(req.getDescriptionHtml())
            .price(req.getPrice())
            .stockQuantity(req.getStockQuantity())
            .thumbnailImageKey(req.getThumbnailImageKey())
            .mainImageKeys(normalizeMainImageKeys(req.getMainImageKeys()))
            .isActive(req.getIsActive() == null ? Boolean.TRUE : req.getIsActive())
            .createdAt(now)
            .updatedAt(now)
            .build();
    productRepository.save(product);
    return toDto(product);
  }

  @Transactional
  public ProductResponse update(Long id, ProductUpdateRequest req) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    product.setName(req.getName());
    product.setDescriptionHtml(req.getDescriptionHtml());
    product.setPrice(req.getPrice());
    product.setStockQuantity(req.getStockQuantity());
    product.setThumbnailImageKey(req.getThumbnailImageKey());
    product.setMainImageKeys(normalizeMainImageKeys(req.getMainImageKeys()));
    product.setIsActive(req.getIsActive());
    product.setUpdatedAt(LocalDateTime.now());
    productRepository.save(product);
    return toDto(product);
  }

  public ProductListResponse sellerList(
      Long sellerId, int page, int limit, String search, String sort, Long userId) {
    assertMemberOfSeller(sellerId, userId);
    if (page < 1) page = 1;
    if (limit < 1) limit = 10;
    PageRequest pageable = PageRequest.of(page - 1, limit, parseSort(sort));
    Page<Product> pg;
    if (search != null && !search.isBlank()) {
      pg =
          productRepository.findBySellerIdAndNameContainingIgnoreCase(
              sellerId, search.trim(), pageable);
    } else {
      pg = productRepository.findBySellerId(sellerId, pageable);
    }
    var list =
        pg.getContent().stream().map(ProductApplicationService::toDto).collect(Collectors.toList());
    return new ProductListResponse(true, list, pg.getTotalElements(), page, limit);
  }

  @Transactional
  public void partialUpdate(Long id, SellerProductUpdateRequest req, Long userId) {
    Product product = findOwnedProduct(id, userId);
    if (req.getName() != null) product.setName(req.getName());
    if (req.getDescriptionHtml() != null) product.setDescriptionHtml(req.getDescriptionHtml());
    if (req.getPrice() != null) product.setPrice(req.getPrice());
    if (req.getStockQuantity() != null) product.setStockQuantity(req.getStockQuantity());
    if (req.getThumbnailImageKey() != null)
      product.setThumbnailImageKey(req.getThumbnailImageKey());
    if (req.getMainImageKeys() != null)
      product.setMainImageKeys(normalizeMainImageKeys(req.getMainImageKeys()));
    if (req.getIsActive() != null) product.setIsActive(req.getIsActive());
    product.setUpdatedAt(LocalDateTime.now());
    productRepository.save(product);
  }

  @Transactional
  public void toggleActive(Long id, Boolean isActive, Long userId) {
    Product product = findOwnedProduct(id, userId);
    product.setIsActive(Boolean.TRUE.equals(isActive));
    product.setUpdatedAt(LocalDateTime.now());
    productRepository.save(product);
  }

  @Transactional
  public void logicalDelete(Long id, Long userId) {
    Product product = findOwnedProduct(id, userId);
    product.setIsActive(false);
    product.setUpdatedAt(LocalDateTime.now());
    productRepository.save(product);
  }

  private static ProductResponse toDto(Product product) {
    return new ProductResponse(
        product.getId(),
        product.getSellerId(),
        product.getName(),
        product.getDescriptionHtml(),
        product.getPrice(),
        product.getStockQuantity(),
        product.getThumbnailImageKey(),
        product.getMainImageKeys() == null ? List.of() : List.copyOf(product.getMainImageKeys()),
        product.getIsActive(),
        product.getCreatedAt(),
        product.getUpdatedAt());
  }

  private static List<String> normalizeMainImageKeys(List<String> keys) {
    if (keys == null) {
      return new ArrayList<>();
    }
    return keys.stream()
        .filter(key -> key != null && !key.isBlank())
        .map(String::trim)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private static Sort parseSort(String sort) {
    String normalized = sort == null ? "" : sort.trim().toLowerCase();
    return switch (normalized) {
      case "created_at_asc" -> Sort.by("createdAt").ascending();
      case "price_asc" -> Sort.by("price").ascending();
      case "price_desc" -> Sort.by("price").descending();
      default -> Sort.by("createdAt").descending();
    };
  }

  private void assertMemberOfSeller(Long sellerId, Long userId) {
    if (!sellerMembershipChecker.isMemberOfSeller(sellerId, userId)) {
      throw new AccessDeniedException("해당 판매자에 대한 권한이 없습니다.");
    }
  }

  private Product findOwnedProduct(Long productId, Long userId) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    assertMemberOfSeller(product.getSellerId(), userId);
    return product;
  }
}
