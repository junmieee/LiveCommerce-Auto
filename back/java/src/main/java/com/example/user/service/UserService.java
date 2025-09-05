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

  public void register(RegisterRequest request) {
    // 이메일 중복 확인
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    // 엔티티 생성 및 매핑
    User user = new User();
    user.setEmail(request.getEmail());
    user.setName(request.getName());
    user.setProvider(Provider.valueOf(request.getProvider().toUpperCase()));
    user.setProviderId(request.getProviderId());
    user.setIsSeller(Boolean.TRUE.equals(request.getIsSeller()));
    user.setCompanyName(request.getCompanyName());
    user.setBusinessNumber(request.getBusinessNumber());
    user.setContactEmail(request.getContactEmail());
    user.setIsActive(true);
    user.setCreatedAt(LocalDateTime.now());

    if ("local".equalsIgnoreCase(request.getProvider())) {
      if (request.getPassword() == null || request.getPassword().isBlank()) {
        throw new IllegalArgumentException("로컬 회원가입 시 비밀번호는 필수입니다.");
      }
      user.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    userRepository.save(user);
  }

  public LoginResponse login(LoginRequest request) {
    Optional<User> opt = userRepository.findByEmail(request.getEmail());
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
