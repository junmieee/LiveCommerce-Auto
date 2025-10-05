package com.example.shipping.controller;

import com.example.shipping.entity.Shipping;
import com.example.shipping.service.ShippingService;
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
@RequestMapping("/shippings")
@Tag(name = "Shippings")
@SecurityRequirement(name = "bearerAuth")
public class ShippingController {
  private final ShippingService service;

  public ShippingController(ShippingService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "배송 목록")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "배송 목록",
        content =
            @Content(array = @ArraySchema(schema = @Schema(implementation = Shipping.class)))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public List<Shipping> getAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  @Operation(summary = "배송 상세")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "배송 상세",
        content = @Content(schema = @Schema(implementation = Shipping.class))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "배송을 찾을 수 없음",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public Shipping getById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @Operation(summary = "배송 생성")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "배송 생성 완료",
        content = @Content(schema = @Schema(implementation = Shipping.class))),
    @ApiResponse(
        responseCode = "400",
        description = "검증 오류",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public Shipping create(@RequestBody Shipping item) {
    return service.save(item);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "배송 삭제")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "배송 삭제 완료"),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }
}
