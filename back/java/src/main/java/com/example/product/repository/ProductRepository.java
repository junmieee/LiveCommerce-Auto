package com.example.product.repository;

import com.example.product.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
  List<Product> findBySellerId(Long sellerId);

  Page<Product> findBySellerId(Long sellerId, Pageable pageable);

  Page<Product> findBySellerIdAndNameContainingIgnoreCase(
      Long sellerId, String name, Pageable pageable);

  Page<Product> findByIsActiveTrue(Pageable pageable);

  Page<Product> findByIsActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

  Optional<Product> findByIdAndIsActiveTrue(Long id);
}
