package com.example.service;

import com.example.entity.OrderItem;
import com.example.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import java.util.List;

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
