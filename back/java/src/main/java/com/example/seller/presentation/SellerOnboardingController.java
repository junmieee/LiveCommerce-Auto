package com.example.seller.presentation;

import com.example.seller.application.SellerOnboardingService;
import com.example.seller.application.command.RegisterSellerWithUserCommand;
import com.example.seller.presentation.dto.SellerRegistrationRequest;
import com.example.user.presentation.dto.response.SimpleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sellers")
@Tag(name = "Sellers")
public class SellerOnboardingController {

  private final SellerOnboardingService sellerOnboardingService;

  public SellerOnboardingController(SellerOnboardingService sellerOnboardingService) {
    this.sellerOnboardingService = sellerOnboardingService;
  }

  @PostMapping("/register")
  @Operation(summary = "판매자 가입")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "판매자 계정 생성 완료",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "검증 오류 또는 중복 데이터",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "이미 판매자 계정 존재",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public ResponseEntity<SimpleResponse> registerSeller(
      @Valid @RequestBody SellerRegistrationRequest request) {

    RegisterSellerWithUserCommand command =
        RegisterSellerWithUserCommand.builder()
            .email(request.getEmail())
            .name(request.getName())
            .password(request.getPassword())
            .companyName(request.getCompanyName())
            .businessNumber(request.getBusinessNumber())
            .contactEmail(request.getContactEmail())
            .contactPhone(request.getContactPhone())
            .settlementCycle(request.getSettlementCycle())
            .payoutDay(request.getPayoutDay())
            .build();

    sellerOnboardingService.registerSellerWithNewUser(command);

    return ResponseEntity.status(201).body(new SimpleResponse(true, "판매자 등록 완료"));
  }
}
