package com.example.order.service;

import com.example.order.entity.Order;
import com.example.order.repository.OrderRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
  private final OrderRepository repository;

  public OrderService(OrderRepository repository) {
    this.repository = repository;
  }

  public List<Order> findAll() {
    return repository.findAll();
  }

  public Order save(Order item) {
    return repository.save(item);
  }

  public Order findById(Long id) {
    return repository.findById(id).orElse(null);
  }

  public void deleteById(Long id) {
    repository.deleteById(id);
  }
}
