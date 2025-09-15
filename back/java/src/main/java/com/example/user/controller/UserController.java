package com.example.user.controller;

import com.example.user.dto.request.*;
import com.example.user.dto.response.*;
import com.example.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  @Operation(summary = "회원가입", description = "local 또는 social(provider) 기반 회원가입")
  public ResponseEntity<SimpleResponse> register(@RequestBody RegisterRequest request) {
    userService.register(request);
    return ResponseEntity.status(201).body(new SimpleResponse(true, "회원가입 완료"));
  }

  @PostMapping("/login")
  @Operation(summary = "로그인", description = "JWT Access/Refresh 발급")
  public ResponseEntity<LoginResponse> login(
      @jakarta.validation.Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(userService.login(request));
  }

  @PostMapping("/refresh")
  @Operation(summary = "토큰 리프레시", description = "리프레시로 새 Access 발급")
  public ResponseEntity<RefreshResponse> refresh(
      @jakarta.validation.Valid @RequestBody RefreshRequest request) {
    return ResponseEntity.ok(userService.refresh(request));
  }

  @PostMapping("/logout")
  @Operation(summary = "로그아웃")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<SimpleResponse> logout(Authentication auth, @RequestParam Long userId) {
    // 인증 후 자신의 userId로 요청한다고 가정
    userService.logout(userId);
    return ResponseEntity.ok(new SimpleResponse(true, "로그아웃 완료"));
  }

  @GetMapping("/me")
  @Operation(summary = "내 정보 조회")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<UserProfileResponse> getMyInfo() {
    // 간단 버전: 클레임만 검증되어 있으므로 실제 사용자 조회는 추후 구현
    return ResponseEntity.ok(new UserProfileResponse());
  }

  @PatchMapping("/profile")
  @Operation(summary = "프로필 수정")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<SimpleResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
    // TODO: 이름, 프로필 이미지 변경 처리
    return ResponseEntity.ok(new SimpleResponse(true, "수정 완료"));
  }

  @DeleteMapping("/withdraw")
  @Operation(summary = "회원 탈퇴")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<SimpleResponse> withdraw() {
    // TODO: is_active → false 처리
    return ResponseEntity.ok(new SimpleResponse(true, "탈퇴 완료"));
  }

  @PatchMapping("/seller")
  @Operation(summary = "판매자 전환 신청")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<SimpleResponse> becomeSeller(@RequestBody SellerRequest request) {
    // TODO: 판매자 전환 처리
    return ResponseEntity.ok(new SimpleResponse(true, "판매자 등록 완료"));
  }

  @GetMapping("/check-email")
  @Operation(summary = "이메일 중복 체크")
  public ResponseEntity<EmailCheckResponse> checkEmail(@RequestParam String email) {
    // TODO: 중복 이메일 검사
    return ResponseEntity.ok(new EmailCheckResponse(false));
  }
}
