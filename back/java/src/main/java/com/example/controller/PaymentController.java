package com.example.controller;

import com.example.entity.Payment;
import com.example.service.PaymentService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Payment> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Payment getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Payment create(@RequestBody Payment item) {
        return service.save(item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
