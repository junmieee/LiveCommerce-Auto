package com.example.seller.presentation;

import com.example.seller.application.SellerMonthlySummaryService;
import com.example.seller.domain.SellerMonthlySummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sellermonthlysummarys")
@Tag(name = "Seller Monthly Summary")
@SecurityRequirement(name = "bearerAuth")
public class SellerMonthlySummaryController {
  private final SellerMonthlySummaryService service;

  public SellerMonthlySummaryController(SellerMonthlySummaryService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "요약 목록")
  public List<SellerMonthlySummary> getAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  @Operation(summary = "요약 상세")
  public SellerMonthlySummary getById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @Operation(summary = "요약 생성")
  public SellerMonthlySummary create(@RequestBody SellerMonthlySummary item) {
    return service.save(item);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "요약 삭제")
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }
}
