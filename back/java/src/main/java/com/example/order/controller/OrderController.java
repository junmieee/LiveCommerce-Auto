package com.example.order.controller;

import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
  private final OrderService service;

  public OrderController(OrderService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "주문 목록")
  public List<Order> getAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  @Operation(summary = "주문 상세")
  public Order getById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @Operation(summary = "주문 생성")
  public Order create(@RequestBody Order item) {
    return service.save(item);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "주문 삭제")
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }
}
