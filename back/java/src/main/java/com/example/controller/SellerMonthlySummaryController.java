package com.example.controller;

import com.example.entity.SellerMonthlySummary;
import com.example.service.SellerMonthlySummaryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/sellermonthlysummarys")
public class SellerMonthlySummaryController {
    private final SellerMonthlySummaryService service;

    public SellerMonthlySummaryController(SellerMonthlySummaryService service) {
        this.service = service;
    }

    @GetMapping
    public List<SellerMonthlySummary> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public SellerMonthlySummary getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public SellerMonthlySummary create(@RequestBody SellerMonthlySummary item) {
        return service.save(item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
