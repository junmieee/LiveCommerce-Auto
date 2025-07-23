package com.example.controller;

import com.example.entity.Shipping;
import com.example.service.ShippingService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/shippings")
public class ShippingController {
    private final ShippingService service;

    public ShippingController(ShippingService service) {
        this.service = service;
    }

    @GetMapping
    public List<Shipping> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Shipping getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Shipping create(@RequestBody Shipping item) {
        return service.save(item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
