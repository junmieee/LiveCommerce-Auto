package com.example.order.service;

import com.example.order.entity.OrderItem;
import com.example.order.repository.OrderItemRepository;
import com.example.product.domain.Product;
import com.example.product.domain.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderItemService {
  private final OrderItemRepository repository;
  private final ProductRepository productRepository;

  public OrderItemService(OrderItemRepository repository, ProductRepository productRepository) {
    this.repository = repository;
    this.productRepository = productRepository;
  }

  public List<OrderItem> findAll() {
    return repository.findAll();
  }

  @Transactional
  public OrderItem save(OrderItem item) {
    reserveStock(item);
    return repository.save(item);
  }

  public OrderItem findById(Long id) {
    return repository.findById(id).orElse(null);
  }

  @Transactional
  public void deleteById(Long id) {
    repository.deleteById(id);
  }

  private void reserveStock(OrderItem item) {
    if (item.getProductId() == null) {
      return;
    }
    int quantity = parseQuantity(item.getQuantity());
    if (quantity <= 0) {
      return;
    }

    Product product =
        productRepository
            .findByIdForUpdate(item.getProductId())
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    int currentStock = product.getStockQuantity() == null ? 0 : product.getStockQuantity();
    if (currentStock < quantity) {
      throw new IllegalStateException("재고가 부족합니다.");
    }
    product.setStockQuantity(currentStock - quantity);
    product.setUpdatedAt(LocalDateTime.now());
    productRepository.save(product);
  }

  private int parseQuantity(String rawQuantity) {
    if (rawQuantity == null) {
      return 0;
    }
    try {
      return Integer.parseInt(rawQuantity);
    } catch (NumberFormatException ex) {
      return 0;
    }
  }
}
