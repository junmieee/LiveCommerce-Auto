package com.example.seller.presentation;

import com.example.seller.application.SellerAuthenticationService;
import com.example.seller.presentation.dto.SellerLoginRequest;
import com.example.seller.presentation.dto.SellerLoginResponse;
import io.swagger.v3.oas.annotations.Operation;
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
  public ResponseEntity<SellerLoginResponse> login(@Valid @RequestBody SellerLoginRequest request) {
    SellerLoginResponse response =
        sellerAuthenticationService.login(request.getEmail(), request.getPassword());
    return ResponseEntity.ok(response);
  }
}
