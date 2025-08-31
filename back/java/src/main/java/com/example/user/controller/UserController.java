package com.example.user.controller;

import com.example.user.dto.request.*;
import com.example.user.dto.response.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @PostMapping("/register")
  public ResponseEntity<SimpleResponse> register(@RequestBody RegisterRequest request) {
    // TODO: UserService에 회원가입 처리 로직 연결
    return ResponseEntity.status(201).body(new SimpleResponse(true, "회원가입 완료"));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    // TODO: 인증 처리 및 JWT 발급
    return ResponseEntity.ok(new LoginResponse(true, "dummy-jwt-token"));
  }

  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> getMyInfo() {
    // TODO: 인증된 사용자 정보 반환
    return ResponseEntity.ok(new UserProfileResponse());
  }

  @PatchMapping("/profile")
  public ResponseEntity<SimpleResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
    // TODO: 이름, 프로필 이미지 변경 처리
    return ResponseEntity.ok(new SimpleResponse(true, "수정 완료"));
  }

  @DeleteMapping("/withdraw")
  public ResponseEntity<SimpleResponse> withdraw() {
    // TODO: is_active → false 처리
    return ResponseEntity.ok(new SimpleResponse(true, "탈퇴 완료"));
  }

  @PatchMapping("/seller")
  public ResponseEntity<SimpleResponse> becomeSeller(@RequestBody SellerRequest request) {
    // TODO: 판매자 전환 처리
    return ResponseEntity.ok(new SimpleResponse(true, "판매자 등록 완료"));
  }

  @GetMapping("/check-email")
  public ResponseEntity<EmailCheckResponse> checkEmail(@RequestParam String email) {
    // TODO: 중복 이메일 검사
    return ResponseEntity.ok(new EmailCheckResponse(false));
  }
}
