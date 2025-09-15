package com.example.shipping.controller;

import com.example.shipping.entity.Shipping;
import com.example.shipping.service.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shippings")
@Tag(name = "Shippings")
@SecurityRequirement(name = "bearerAuth")
public class ShippingController {
  private final ShippingService service;

  public ShippingController(ShippingService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "배송 목록")
  public List<Shipping> getAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  @Operation(summary = "배송 상세")
  public Shipping getById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @Operation(summary = "배송 생성")
  public Shipping create(@RequestBody Shipping item) {
    return service.save(item);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "배송 삭제")
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }
}
