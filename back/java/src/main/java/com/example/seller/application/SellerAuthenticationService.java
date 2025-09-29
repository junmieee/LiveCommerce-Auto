package com.example.seller.application;

import com.example.security.JwtTokenProvider;
import com.example.security.RefreshTokenStore;
import com.example.seller.domain.Seller;
import com.example.seller.domain.SellerMember;
import com.example.seller.domain.SellerMembershipRepository;
import com.example.seller.domain.SellerRepository;
import com.example.seller.presentation.dto.SellerLoginResponse;
import com.example.user.domain.User;
import com.example.user.domain.UserRepository;
import com.example.user.domain.enums.Provider;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SellerAuthenticationService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final SellerMembershipRepository sellerMembershipRepository;
  private final SellerRepository sellerRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenStore refreshTokenStore;

  public SellerAuthenticationService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      SellerMembershipRepository sellerMembershipRepository,
      SellerRepository sellerRepository,
      JwtTokenProvider jwtTokenProvider,
      RefreshTokenStore refreshTokenStore) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.sellerMembershipRepository = sellerMembershipRepository;
    this.sellerRepository = sellerRepository;
    this.jwtTokenProvider = jwtTokenProvider;
    this.refreshTokenStore = refreshTokenStore;
  }

  @Transactional
  public SellerLoginResponse login(String rawEmail, String rawPassword) {
    if (rawEmail == null || rawEmail.isBlank()) {
      throw new IllegalArgumentException("이메일은 필수입니다.");
    }
    if (rawPassword == null || rawPassword.isBlank()) {
      throw new IllegalArgumentException("비밀번호는 필수입니다.");
    }

    String email = rawEmail.trim().toLowerCase();

    User user =
        userRepository
            .findByEmailIgnoreCase(email)
            .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

    if (user.getProvider() != null && user.getProvider() != Provider.LOCAL) {
      throw new IllegalArgumentException("판매자 로그인은 로컬 계정만 지원합니다.");
    }
    if (user.getPassword() == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
      throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
    if (Boolean.FALSE.equals(user.getIsActive())) {
      throw new AccessDeniedException("비활성화된 계정입니다.");
    }

    List<SellerMember> memberships = sellerMembershipRepository.findByUserId(user.getId());
    if (memberships == null || memberships.isEmpty()) {
      throw new AccessDeniedException("판매자 권한이 없습니다.");
    }

    List<Long> sellerIds =
        memberships.stream()
            .map(m -> m.getId().getSellerId())
            .distinct()
            .collect(Collectors.toList());
    List<Seller> sellers =
        sellerIds.isEmpty() ? Collections.emptyList() : sellerRepository.findByIdIn(sellerIds);
    Map<Long, Seller> sellerMap = sellers.stream().collect(Collectors.toMap(Seller::getId, s -> s));

    String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
    String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
    refreshTokenStore.store(user.getId(), refreshToken);

    user.setLastLoginAt(LocalDateTime.now());
    userRepository.save(user);

    List<SellerLoginResponse.SellerAccount> sellerAccounts =
        memberships.stream()
            .map(
                membership -> {
                  Long sellerId = membership.getId().getSellerId();
                  Seller seller = sellerMap.get(sellerId);
                  return SellerLoginResponse.SellerAccount.builder()
                      .sellerId(sellerId)
                      .sellerName(seller != null ? seller.getName() : null)
                      .role(membership.getRole())
                      .defaultAccount(Boolean.TRUE.equals(membership.getIsDefault()))
                      .status(seller != null ? seller.getStatus() : null)
                      .build();
                })
            .collect(Collectors.toList());

    return SellerLoginResponse.builder()
        .success(true)
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .userId(user.getId())
        .sellers(sellerAccounts)
        .build();
  }
}
