package com.example.shipping.service;

import com.example.shipping.entity.Shipping;
import com.example.shipping.repository.ShippingRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ShippingService {
  private final ShippingRepository repository;

  public ShippingService(ShippingRepository repository) {
    this.repository = repository;
  }

  public List<Shipping> findAll() {
    return repository.findAll();
  }

  public Shipping save(Shipping item) {
    return repository.save(item);
  }

  public Shipping findById(Long id) {
    return repository.findById(id).orElse(null);
  }

  public void deleteById(Long id) {
    repository.deleteById(id);
  }
}
