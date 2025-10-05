package com.example.payment.controller;

import com.example.payment.entity.Payment;
import com.example.payment.service.PaymentService;
import com.example.user.presentation.dto.response.SimpleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "결제 목록",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Payment.class)))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public List<Payment> getAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  @Operation(summary = "결제 상세")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "결제 상세",
        content = @Content(schema = @Schema(implementation = Payment.class))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "결제를 찾을 수 없음",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public Payment getById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @Operation(summary = "결제 생성")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "결제 생성 완료",
        content = @Content(schema = @Schema(implementation = Payment.class))),
    @ApiResponse(
        responseCode = "400",
        description = "검증 오류",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public Payment create(@RequestBody Payment item) {
    return service.save(item);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "결제 삭제")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "결제 삭제 완료"),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }
}
