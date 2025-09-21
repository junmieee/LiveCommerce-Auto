package com.example.seller.service;

import com.example.seller.entity.Seller;
import com.example.seller.entity.SellerMember;
import com.example.seller.entity.SellerMemberId;
import com.example.seller.repository.SellerMemberRepository;
import com.example.seller.repository.SellerRepository;
import com.example.user.dto.request.SellerRequest;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SellerOnboardingService {

  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\\s]+@[^@\\\s]+\\.[^@\\\s]+$");

  private final UserRepository userRepository;
  private final SellerRepository sellerRepository;
  private final SellerMemberRepository sellerMemberRepository;

  public SellerOnboardingService(
      UserRepository userRepository,
      SellerRepository sellerRepository,
      SellerMemberRepository sellerMemberRepository) {
    this.userRepository = userRepository;
    this.sellerRepository = sellerRepository;
    this.sellerMemberRepository = sellerMemberRepository;
  }

  @Transactional
  public void registerSeller(Long userId, SellerRequest request) {
    if (userId == null || userId <= 0) {
      throw new IllegalArgumentException("사용자 ID는 필수입니다.");
    }
    if (request == null) {
      throw new IllegalArgumentException("요청이 올바르지 않습니다.");
    }

    String companyName = normalize(request.getCompanyName());
    String businessNumber = normalize(request.getBusinessNumber());
    String contactEmail = normalize(request.getContactEmail());
    String contactPhone = normalize(request.getContactPhone());
    String settlementCycle = normalize(request.getSettlementCycle());
    Short payoutDay = request.getPayoutDay();

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

    if (sellerMemberRepository.existsByIdUserId(userId)) {
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
            .id(new SellerMemberId(savedSeller.getId(), user.getId()))
            .role("OWNER")
            .isDefault(true)
            .joinedAt(now)
            .build();
    sellerMemberRepository.save(member);

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
}
