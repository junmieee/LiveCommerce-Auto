package com.example.payment.service;

import com.example.payment.entity.Payment;
import com.example.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public List<Payment> findAll() {
        return repository.findAll();
    }

    public Payment save(Payment item) {
        return repository.save(item);
    }

    public Payment findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
