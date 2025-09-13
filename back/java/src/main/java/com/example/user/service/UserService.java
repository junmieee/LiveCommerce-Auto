package com.example.user.service;

import com.example.security.JwtTokenProvider;
import com.example.security.RefreshTokenStore;
import com.example.user.dto.request.LoginRequest;
import com.example.user.dto.request.RefreshRequest;
import com.example.user.dto.request.RegisterRequest;
import com.example.user.dto.response.LoginResponse;
import com.example.user.dto.response.RefreshResponse;
import com.example.user.entity.User;
import com.example.user.entity.enums.Provider;
import com.example.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenStore refreshTokenStore;

  @org.springframework.transaction.annotation.Transactional
  public void register(RegisterRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청이 올바르지 않습니다.");
    }

    String rawEmail = request.getEmail();
    String email = rawEmail == null ? null : rawEmail.trim().toLowerCase();
    String name = request.getName() == null ? null : request.getName().trim();
    String providerStr = request.getProvider() == null ? null : request.getProvider().trim();
    String providerId = request.getProviderId() == null ? null : request.getProviderId().trim();
    Boolean wantSeller = Boolean.TRUE.equals(request.getIsSeller());

    if (email == null || email.isBlank() || !email.matches("^[^@\\\s]+@[^@\\\s]+\\.[^@\\\s]+$")) {
      throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("이름은 필수입니다.");
    }
    if (providerStr == null || providerStr.isBlank()) {
      throw new IllegalArgumentException("provider는 필수입니다.");
    }

    Provider provider = Provider.from(providerStr);

    if (userRepository.existsByEmailIgnoreCase(email)) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    if (provider != Provider.LOCAL) {
      if (providerId == null || providerId.isBlank()) {
        throw new IllegalArgumentException("소셜 회원가입 시 providerId는 필수입니다.");
      }
      if (userRepository.existsByProviderAndProviderId(provider, providerId)) {
        throw new IllegalArgumentException("이미 연동된 소셜 계정입니다.");
      }
      if (request.getPassword() != null && !request.getPassword().isBlank()) {
        throw new IllegalArgumentException("소셜 회원가입에는 비밀번호를 사용할 수 없습니다.");
      }
    } else {
      if (request.getPassword() == null || request.getPassword().isBlank()) {
        throw new IllegalArgumentException("로컬 회원가입 시 비밀번호는 필수입니다.");
      }
      if (request.getPassword().length() < 8) {
        throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
      }
    }

    // 판매자 정보(company/business/contact)는 V09~V12에서 sellers 테이블로 분리됨
    // 여기서는 isSeller 플래그만 세팅하고, 실제 Seller/SellerMember 생성은 별도 API에서 처리 예정

    User user = new User();
    user.setEmail(email);
    user.setName(name);
    user.setProvider(provider);
    user.setProviderId(providerId);
    user.setIsSeller(wantSeller);
    user.setIsActive(true);
    user.setCreatedAt(LocalDateTime.now());

    if (provider == Provider.LOCAL) {
      user.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    userRepository.save(user);
  }

  public LoginResponse login(LoginRequest request) {
    Optional<User> opt = userRepository.findByEmailIgnoreCase(request.getEmail());
    User user = opt.orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));
    if (user.getPassword() == null
        || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
    user.setLastLoginAt(LocalDateTime.now());
    userRepository.save(user);

    String access = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
    String refresh = jwtTokenProvider.generateRefreshToken(user.getId());
    refreshTokenStore.store(user.getId(), refresh);

    return new LoginResponse(true, access, refresh);
  }

  public RefreshResponse refresh(RefreshRequest request) {
    String saved = refreshTokenStore.get(request.getUserId());
    if (saved == null || !saved.equals(request.getRefreshToken())) {
      throw new IllegalArgumentException("리프레시 토큰이 유효하지 않습니다.");
    }
    Optional<User> opt = userRepository.findById(request.getUserId());
    User user = opt.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    String newAccess = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
    return new RefreshResponse(true, newAccess);
  }

  public void logout(Long userId) {
    refreshTokenStore.delete(userId);
  }
}
