package com.example.product.controller;

import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  @GetMapping
  public List<Product> getAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public Product getById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  public Product create(@RequestBody Product item) {
    return service.save(item);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }
}
