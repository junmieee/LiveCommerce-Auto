package com.example.order.controller;

import com.example.order.entity.OrderItem;
import com.example.order.service.OrderItemService;
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
@RequestMapping("/orderitems")
@Tag(name = "Order Items")
@SecurityRequirement(name = "bearerAuth")
public class OrderItemController {
  private final OrderItemService service;

  public OrderItemController(OrderItemService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "주문 아이템 목록")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "주문 아이템 목록",
        content =
            @Content(array = @ArraySchema(schema = @Schema(implementation = OrderItem.class)))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public List<OrderItem> getAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  @Operation(summary = "주문 아이템 상세")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "주문 아이템 상세",
        content = @Content(schema = @Schema(implementation = OrderItem.class))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "주문 아이템을 찾을 수 없음",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public OrderItem getById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @Operation(summary = "주문 아이템 생성")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "주문 아이템 생성 완료",
        content = @Content(schema = @Schema(implementation = OrderItem.class))),
    @ApiResponse(
        responseCode = "400",
        description = "검증 오류",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public OrderItem create(@RequestBody OrderItem item) {
    return service.save(item);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "주문 아이템 삭제")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "주문 아이템 삭제 완료"),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }
}
