package com.example.common.api;

public final class ApiExampleConstants {
  private ApiExampleConstants() {}

  public static final String SUCCESS_GENERIC =
      "{\"success\":true,\"message\":\"요청이 성공적으로 처리되었습니다.\"}";
  public static final String SUCCESS_REGISTER = "{\"success\":true,\"message\":\"회원가입 완료\"}";
  public static final String SUCCESS_LOGOUT = "{\"success\":true,\"message\":\"로그아웃 완료\"}";
  public static final String SUCCESS_PROFILE_UPDATE = "{\"success\":true,\"message\":\"수정 완료\"}";
  public static final String SUCCESS_WITHDRAW = "{\"success\":true,\"message\":\"탈퇴 완료\"}";
  public static final String SUCCESS_PRODUCT_UPDATE =
      "{\"success\":true,\"message\":\"상품 정보가 수정되었습니다.\"}";
  public static final String SUCCESS_PRODUCT_TOGGLE =
      "{\"success\":true,\"message\":\"상품 상태가 변경되었습니다.\"}";
  public static final String SUCCESS_PRODUCT_DELETE =
      "{\"success\":true,\"message\":\"상품이 삭제되었습니다.\"}";

  public static final String ERROR_GENERIC = "{\"success\":false,\"message\":\"요청이 올바르지 않습니다.\"}";
  public static final String ERROR_UNAUTHORIZED =
      "{\"success\":false,\"message\":\"인증 정보가 필요합니다.\"}";
  public static final String ERROR_FORBIDDEN =
      "{\"success\":false,\"message\":\"해당 판매자에 대한 권한이 없습니다.\"}";
  public static final String ERROR_NOT_FOUND_PRODUCT =
      "{\"success\":false,\"message\":\"상품을 찾을 수 없습니다.\"}";
  public static final String ERROR_LOGIN =
      "{\"success\":false,\"message\":\"이메일 또는 비밀번호가 올바르지 않습니다.\"}";
  public static final String ERROR_REFRESH_TOKEN =
      "{\"success\":false,\"message\":\"리프레시 토큰이 유효하지 않습니다.\"}";
  public static final String ERROR_EMAIL_DUPLICATE =
      "{\"success\":false,\"message\":\"이미 존재하는 이메일입니다.\"}";
  public static final String ERROR_EMAIL_FORMAT =
      "{\"success\":false,\"message\":\"이메일 형식이 올바르지 않습니다.\"}";
  public static final String ERROR_VALIDATION = "{\"success\":false,\"message\":\"검증에 실패했습니다.\"}";
  public static final String ERROR_SELLER_EXISTS =
      "{\"success\":false,\"message\":\"이미 판매자 계정이 존재합니다.\"}";
}
