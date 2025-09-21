package com.example.product.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

  Page<Product> findByIsActiveTrue(Pageable pageable);

  Page<Product> findByIsActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

  Optional<Product> findById(Long id);

  Optional<Product> findByIdAndIsActiveTrue(Long id);

  Page<Product> findBySellerId(Long sellerId, Pageable pageable);

  Page<Product> findBySellerIdAndNameContainingIgnoreCase(
      Long sellerId, String name, Pageable pageable);

  Product save(Product product);
}
