package com.example.order.controller;

import com.example.order.entity.OrderItem;
import com.example.order.service.OrderItemService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orderitems")
public class OrderItemController {
  private final OrderItemService service;

  public OrderItemController(OrderItemService service) {
    this.service = service;
  }

  @GetMapping
  public List<OrderItem> getAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public OrderItem getById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  public OrderItem create(@RequestBody OrderItem item) {
    return service.save(item);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }
}
