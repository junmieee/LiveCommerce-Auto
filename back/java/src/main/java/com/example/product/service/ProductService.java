package com.example.product.service;

import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
  private final ProductRepository repository;

  public ProductService(ProductRepository repository) {
    this.repository = repository;
  }

  public List<Product> findAll() {
    return repository.findAll();
  }

  public Product save(Product item) {
    return repository.save(item);
  }

  public Product findById(Long id) {
    return repository.findById(id).orElse(null);
  }

  public void deleteById(Long id) {
    repository.deleteById(id);
  }
}
