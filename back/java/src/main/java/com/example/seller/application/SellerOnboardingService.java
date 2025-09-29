package com.example.seller.application;

import com.example.seller.application.command.RegisterSellerCommand;
import com.example.seller.application.command.RegisterSellerWithUserCommand;
import com.example.seller.domain.Seller;
import com.example.seller.domain.SellerMember;
import com.example.seller.domain.SellerMemberId;
import com.example.seller.domain.SellerMembershipRepository;
import com.example.seller.domain.SellerRepository;
import com.example.user.domain.User;
import com.example.user.domain.UserRepository;
import com.example.user.domain.enums.Provider;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SellerOnboardingService {

  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

  private final UserRepository userRepository;
  private final SellerRepository sellerRepository;
  private final SellerMembershipRepository sellerMembershipRepository;
  private final PasswordEncoder passwordEncoder;

  public SellerOnboardingService(
      UserRepository userRepository,
      SellerRepository sellerRepository,
      SellerMembershipRepository sellerMembershipRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.sellerRepository = sellerRepository;
    this.sellerMembershipRepository = sellerMembershipRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public void registerSeller(RegisterSellerCommand command) {
    Long userId = command.getUserId();
    if (userId == null || userId <= 0) {
      throw new IllegalArgumentException("사용자 ID는 필수입니다.");
    }

    String companyName = normalize(command.getCompanyName());
    String businessNumber = normalize(command.getBusinessNumber());
    String contactEmail = normalize(command.getContactEmail());
    String contactPhone = normalize(command.getContactPhone());
    String settlementCycle = normalize(command.getSettlementCycle());
    Short payoutDay = command.getPayoutDay();

    if (companyName == null) {
      throw new IllegalArgumentException("상호명은 필수입니다.");
    }
    if (businessNumber == null) {
      throw new IllegalArgumentException("사업자 등록번호는 필수입니다.");
    }
    if (contactEmail != null && !EMAIL_PATTERN.matcher(contactEmail).matches()) {
      throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
    }

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    createSellerForUser(
        user, companyName, businessNumber, contactEmail, contactPhone, settlementCycle, payoutDay);
  }

  @Transactional
  public void registerSellerWithNewUser(RegisterSellerWithUserCommand command) {
    if (command == null) {
      throw new IllegalArgumentException("요청이 올바르지 않습니다.");
    }

    String email = normalizeEmail(command.getEmail());
    if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
      throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
    }
    if (userRepository.existsByEmailIgnoreCase(email)) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    String name = normalize(command.getName());
    if (name == null) {
      throw new IllegalArgumentException("이름은 필수입니다.");
    }

    String rawPassword = command.getPassword();
    if (rawPassword == null) {
      throw new IllegalArgumentException("비밀번호는 필수입니다.");
    }
    String password = rawPassword.trim();
    if (password.length() < 8) {
      throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
    }

    String companyName = normalize(command.getCompanyName());
    String businessNumber = normalize(command.getBusinessNumber());
    String contactEmail = normalize(command.getContactEmail());
    String contactPhone = normalize(command.getContactPhone());
    String settlementCycle = normalize(command.getSettlementCycle());
    Short payoutDay = command.getPayoutDay();

    if (companyName == null) {
      throw new IllegalArgumentException("상호명은 필수입니다.");
    }
    if (businessNumber == null) {
      throw new IllegalArgumentException("사업자 등록번호는 필수입니다.");
    }
    if (contactEmail == null) {
      contactEmail = email;
    } else if (!EMAIL_PATTERN.matcher(contactEmail).matches()) {
      throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
    }

    LocalDateTime now = LocalDateTime.now();

    User user =
        User.builder()
            .email(email)
            .name(name)
            .provider(Provider.LOCAL)
            .password(passwordEncoder.encode(password))
            .isActive(true)
            .isSeller(false)
            .createdAt(now)
            .build();

    User savedUser = userRepository.save(user);

    createSellerForUser(
        savedUser,
        companyName,
        businessNumber,
        contactEmail,
        contactPhone,
        settlementCycle,
        payoutDay);
  }

  private void createSellerForUser(
      User user,
      String companyName,
      String businessNumber,
      String contactEmail,
      String contactPhone,
      String settlementCycle,
      Short payoutDay) {

    Long userId = user.getId();
    if (userId == null) {
      throw new IllegalStateException("사용자 ID를 확인할 수 없습니다.");
    }

    if (sellerMembershipRepository.existsByUserId(userId)) {
      throw new IllegalStateException("이미 판매자 계정이 존재합니다.");
    }

    if (sellerRepository.existsByBusinessNumber(businessNumber)) {
      throw new IllegalArgumentException("이미 등록된 사업자 번호입니다.");
    }

    LocalDateTime now = LocalDateTime.now();
    String finalSettlementCycle =
        settlementCycle == null ? "MONTHLY" : settlementCycle.toUpperCase();

    Seller seller =
        Seller.builder()
            .name(companyName)
            .businessNumber(businessNumber)
            .contactEmail(contactEmail)
            .contactPhone(contactPhone)
            .status("PENDING")
            .commissionRate(BigDecimal.ZERO)
            .settlementCycle(finalSettlementCycle)
            .payoutDay(payoutDay)
            .createdAt(now)
            .updatedAt(now)
            .build();

    Seller savedSeller = sellerRepository.save(seller);

    SellerMember member =
        SellerMember.builder()
            .id(new SellerMemberId(savedSeller.getId(), userId))
            .role("OWNER")
            .isDefault(true)
            .joinedAt(now)
            .build();
    sellerMembershipRepository.save(member);

    user.setIsSeller(true);
    userRepository.save(user);
  }

  private String normalize(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  private String normalizeEmail(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim().toLowerCase();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
