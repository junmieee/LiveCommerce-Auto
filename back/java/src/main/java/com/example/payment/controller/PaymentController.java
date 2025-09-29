package com.example.payment.controller;

import com.example.payment.entity.Payment;
import com.example.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payments")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
  private final PaymentService service;

  public PaymentController(PaymentService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "결제 목록")
  public List<Payment> getAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  @Operation(summary = "결제 상세")
  public Payment getById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @Operation(summary = "결제 생성")
  public Payment create(@RequestBody Payment item) {
    return service.save(item);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "결제 삭제")
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }
}
