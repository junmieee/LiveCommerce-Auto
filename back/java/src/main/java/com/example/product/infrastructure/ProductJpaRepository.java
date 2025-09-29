package com.example.product.infrastructure;

import com.example.product.domain.Product;
import com.example.product.domain.ProductRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long>, ProductRepository {

  @Override
  Page<Product> findBySellerId(Long sellerId, Pageable pageable);

  @Override
  Page<Product> findBySellerIdAndNameContainingIgnoreCase(
      Long sellerId, String name, Pageable pageable);

  @Override
  Page<Product> findByIsActiveTrue(Pageable pageable);

  @Override
  Page<Product> findByIsActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

  @Override
  Optional<Product> findByIdAndIsActiveTrue(Long id);
}
