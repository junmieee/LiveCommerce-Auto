package com.example.payment.controller;

import com.example.payment.entity.Payment;
import com.example.payment.service.PaymentService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

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
