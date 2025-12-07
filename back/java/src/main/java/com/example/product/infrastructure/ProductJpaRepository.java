package com.example.product.infrastructure;

import com.example.product.domain.Product;
import com.example.product.domain.ProductRepository;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long>, ProductRepository {

  @Override
  Page<Product> findBySellerId(Long sellerId, Pageable pageable);

  @Override
  Page<Product> findBySellerIdAndNameContainingIgnoreCase(
      Long sellerId, String name, Pageable pageable);

  @Override
  long countBySellerId(Long sellerId);

  @Override
  long countBySellerIdAndIsActiveTrue(Long sellerId);

  @Override
  @Query("select coalesce(sum(p.stockQuantity),0) from Product p where p.sellerId = :sellerId")
  Long sumStockQuantityBySellerId(Long sellerId);

  @Override
  List<Product> findTop5BySellerIdOrderByUpdatedAtDesc(Long sellerId);

  @Override
  Page<Product> findByIsActiveTrue(Pageable pageable);

  @Override
  Page<Product> findByIsActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

  @Override
  Optional<Product> findByIdAndIsActiveTrue(Long id);

  @Override
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select p from Product p where p.id = :id")
  Optional<Product> findByIdForUpdate(@Param("id") Long id);
}
