package com.example.order.controller;

import com.example.order.entity.OrderItem;
import com.example.order.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orderitems")
@Tag(name = "Order Items")
@SecurityRequirement(name = "bearerAuth")
public class OrderItemController {
  private final OrderItemService service;

  public OrderItemController(OrderItemService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "주문 아이템 목록")
  public List<OrderItem> getAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  @Operation(summary = "주문 아이템 상세")
  public OrderItem getById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @Operation(summary = "주문 아이템 생성")
  public OrderItem create(@RequestBody OrderItem item) {
    return service.save(item);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "주문 아이템 삭제")
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }
}
