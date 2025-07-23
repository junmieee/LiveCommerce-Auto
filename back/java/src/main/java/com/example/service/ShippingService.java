package com.example.service;

import com.example.entity.Shipping;
import com.example.repository.ShippingRepository;
import org.springframework.stereotype.Service;
import java.util.List;

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
