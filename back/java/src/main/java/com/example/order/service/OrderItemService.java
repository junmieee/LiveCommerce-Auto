package com.example.order.service;

import com.example.order.entity.OrderItem;
import com.example.order.repository.OrderItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {
  private final OrderItemRepository repository;

  public OrderItemService(OrderItemRepository repository) {
    this.repository = repository;
  }

  public List<OrderItem> findAll() {
    return repository.findAll();
  }

  public OrderItem save(OrderItem item) {
    return repository.save(item);
  }

  public OrderItem findById(Long id) {
    return repository.findById(id).orElse(null);
  }

  public void deleteById(Long id) {
    repository.deleteById(id);
  }
}
