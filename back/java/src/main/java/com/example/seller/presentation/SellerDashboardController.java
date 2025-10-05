package com.example.seller.presentation;

import com.example.security.SecurityUtils;
import com.example.seller.application.SellerDashboardService;
import com.example.seller.presentation.dto.SellerManagementResponse;
import com.example.seller.presentation.dto.SellerOrdersResponse;
import com.example.seller.presentation.dto.SellerShippingsResponse;
import com.example.user.presentation.dto.response.SimpleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sellers/{sellerId}")
@Tag(name = "Seller Dashboard")
@SecurityRequirement(name = "bearerAuth")
public class SellerDashboardController {

  private final SellerDashboardService sellerDashboardService;

  public SellerDashboardController(SellerDashboardService sellerDashboardService) {
    this.sellerDashboardService = sellerDashboardService;
  }

  @GetMapping("/management")
  @Operation(summary = "판매관리 현황")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "판매관리 현황",
        content = @Content(schema = @Schema(implementation = SellerManagementResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "403",
        description = "판매자 멤버십 권한 없음",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public ResponseEntity<SellerManagementResponse> management(
      Authentication authentication, @PathVariable Long sellerId) {
    Long userId = SecurityUtils.requireAuthUser(authentication).getId();
    return ResponseEntity.ok(sellerDashboardService.management(sellerId, userId));
  }

  @GetMapping("/orders")
  @Operation(summary = "판매자 주문 목록")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "판매자 주문 목록",
        content = @Content(schema = @Schema(implementation = SellerOrdersResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "403",
        description = "판매자 멤버십 권한 없음",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public ResponseEntity<SellerOrdersResponse> orders(
      Authentication authentication, @PathVariable Long sellerId) {
    Long userId = SecurityUtils.requireAuthUser(authentication).getId();
    return ResponseEntity.ok(sellerDashboardService.orders(sellerId, userId));
  }

  @GetMapping("/shippings")
  @Operation(summary = "판매자 배송 목록")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "판매자 배송 목록",
        content = @Content(schema = @Schema(implementation = SellerShippingsResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "403",
        description = "판매자 멤버십 권한 없음",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public ResponseEntity<SellerShippingsResponse> shippings(
      Authentication authentication, @PathVariable Long sellerId) {
    Long userId = SecurityUtils.requireAuthUser(authentication).getId();
    return ResponseEntity.ok(sellerDashboardService.shippings(sellerId, userId));
  }
}
