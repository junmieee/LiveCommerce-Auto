package com.example.seller.presentation;

import com.example.common.api.ApiExampleConstants;
import com.example.seller.application.SellerAuthenticationService;
import com.example.seller.presentation.dto.SellerLoginRequest;
import com.example.seller.presentation.dto.SellerLoginResponse;
import com.example.user.presentation.dto.response.SimpleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
@Tag(name = "Seller Auth")
public class SellerAuthController {

  private final SellerAuthenticationService sellerAuthenticationService;

  public SellerAuthController(SellerAuthenticationService sellerAuthenticationService) {
    this.sellerAuthenticationService = sellerAuthenticationService;
  }

  @PostMapping("/login")
  @Operation(summary = "판매자 로그인")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "판매자 로그인 성공",
        content = @Content(schema = @Schema(implementation = SellerLoginResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 로그인 정보",
        content =
            @Content(
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "SellerLoginBadRequest",
                        value = ApiExampleConstants.ERROR_LOGIN))),
    @ApiResponse(
        responseCode = "403",
        description = "판매자 권한 없음",
        content =
            @Content(
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "SellerLoginForbidden",
                        value = ApiExampleConstants.ERROR_FORBIDDEN)))
  })
  public ResponseEntity<SellerLoginResponse> login(@Valid @RequestBody SellerLoginRequest request) {
    SellerLoginResponse response =
        sellerAuthenticationService.login(request.getEmail(), request.getPassword());
    return ResponseEntity.ok(response);
  }
}
