package com.example.order.controller;

import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
  private final OrderService service;

  public OrderController(OrderService service) {
    this.service = service;
  }

  @GetMapping
  public List<Order> getAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public Order getById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  public Order create(@RequestBody Order item) {
    return service.save(item);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }
}
