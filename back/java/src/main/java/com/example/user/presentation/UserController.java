package com.example.user.presentation;

import com.example.common.api.ApiExampleConstants;
import com.example.user.application.UserApplicationService;
import com.example.user.presentation.dto.request.*;
import com.example.user.presentation.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users")
public class UserController {

  private final UserApplicationService userService;

  public UserController(UserApplicationService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  @Operation(summary = "회원가입", description = "local 또는 social(provider) 기반 회원가입")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "회원가입 완료",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "RegisterSuccess",
                        value = ApiExampleConstants.SUCCESS_REGISTER))),
    @ApiResponse(
        responseCode = "400",
        description = "검증 오류 또는 중복 이메일",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "RegisterFailed",
                        value = ApiExampleConstants.ERROR_EMAIL_DUPLICATE)))
  })
  public ResponseEntity<SimpleResponse> register(@RequestBody RegisterRequest request) {
    userService.register(request);
    return ResponseEntity.status(201).body(new SimpleResponse(true, "회원가입 완료"));
  }

  @PostMapping("/login")
  @Operation(summary = "로그인", description = "JWT Access/Refresh 발급")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "로그인 성공",
        content = @Content(schema = @Schema(implementation = LoginResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 로그인 정보",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(name = "LoginFailed", value = ApiExampleConstants.ERROR_LOGIN)))
  })
  public ResponseEntity<LoginResponse> login(
      @jakarta.validation.Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(userService.login(request));
  }

  @PostMapping("/refresh")
  @Operation(summary = "토큰 리프레시", description = "리프레시로 새 Access 발급")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "새 액세스 토큰 발급",
        content = @Content(schema = @Schema(implementation = RefreshResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "리프레시 토큰 오류",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "RefreshFailed",
                        value = ApiExampleConstants.ERROR_REFRESH_TOKEN))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 만료",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "RefreshUnauthorized",
                        value = ApiExampleConstants.ERROR_UNAUTHORIZED)))
  })
  public ResponseEntity<RefreshResponse> refresh(
      @jakarta.validation.Valid @RequestBody RefreshRequest request) {
    return ResponseEntity.ok(userService.refresh(request));
  }

  @PostMapping("/logout")
  @Operation(summary = "로그아웃")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "로그아웃 완료",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "LogoutSuccess",
                        value = ApiExampleConstants.SUCCESS_LOGOUT))),
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 사용자 ID",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "LogoutInvalid",
                        value = ApiExampleConstants.ERROR_GENERIC))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "LogoutUnauthorized",
                        value = ApiExampleConstants.ERROR_UNAUTHORIZED)))
  })
  public ResponseEntity<SimpleResponse> logout(Authentication auth, @RequestParam Long userId) {
    userService.logout(userId);
    return ResponseEntity.ok(new SimpleResponse(true, "로그아웃 완료"));
  }

  @GetMapping("/me")
  @Operation(summary = "내 정보 조회")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "내 정보",
        content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "ProfileUnauthorized",
                        value = ApiExampleConstants.ERROR_UNAUTHORIZED)))
  })
  public ResponseEntity<UserProfileResponse> getMyInfo() {
    return ResponseEntity.ok(new UserProfileResponse());
  }

  @PatchMapping("/profile")
  @Operation(summary = "프로필 수정")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "프로필 수정 완료",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "ProfileUpdated",
                        value = ApiExampleConstants.SUCCESS_PROFILE_UPDATE))),
    @ApiResponse(
        responseCode = "400",
        description = "검증 오류",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "ProfileValidation",
                        value = ApiExampleConstants.ERROR_VALIDATION))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "ProfileUnauthorized",
                        value = ApiExampleConstants.ERROR_UNAUTHORIZED)))
  })
  public ResponseEntity<SimpleResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
    return ResponseEntity.ok(new SimpleResponse(true, "수정 완료"));
  }

  @DeleteMapping("/withdraw")
  @Operation(summary = "회원 탈퇴")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "회원 탈퇴 완료",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "WithdrawSuccess",
                        value = ApiExampleConstants.SUCCESS_WITHDRAW))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "WithdrawUnauthorized",
                        value = ApiExampleConstants.ERROR_UNAUTHORIZED)))
  })
  public ResponseEntity<SimpleResponse> withdraw() {
    return ResponseEntity.ok(new SimpleResponse(true, "탈퇴 완료"));
  }

  @GetMapping("/check-email")
  @Operation(summary = "이메일 중복 체크")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "중복 여부",
        content = @Content(schema = @Schema(implementation = EmailCheckResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 이메일 형식",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SimpleResponse.class),
                examples =
                    @ExampleObject(
                        name = "EmailInvalid",
                        value = ApiExampleConstants.ERROR_EMAIL_FORMAT)))
  })
  public ResponseEntity<EmailCheckResponse> checkEmail(@RequestParam String email) {
    return ResponseEntity.ok(new EmailCheckResponse(false));
  }
}
