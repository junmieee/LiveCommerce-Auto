package com.example.seller.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.seller.entity.Seller;
import com.example.seller.entity.SellerMember;
import com.example.seller.entity.SellerMemberId;
import com.example.seller.repository.SellerMemberRepository;
import com.example.seller.repository.SellerRepository;
import com.example.user.dto.request.SellerRequest;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellerOnboardingServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private SellerRepository sellerRepository;
  @Mock private SellerMemberRepository sellerMemberRepository;

  @InjectMocks private SellerOnboardingService service;

  private SellerRequest request;
  private User user;

  @BeforeEach
  void setUp() {
    request = new SellerRequest();
    request.setCompanyName("테스트상사");
    request.setBusinessNumber("123-45-67890");
    request.setContactEmail("seller@test.com");
    request.setContactPhone("010-1234-5678");
    request.setSettlementCycle("monthly");
    request.setPayoutDay((short) 15);

    user = User.builder().id(10L).email("user@test.com").isSeller(false).build();
  }

  @Test
  void registerSeller_createsSellerAndMemberAndMarksUser() {
    when(userRepository.findById(10L)).thenReturn(Optional.of(user));
    when(sellerMemberRepository.existsByIdUserId(10L)).thenReturn(false);
    when(sellerRepository.existsByBusinessNumber("123-45-67890")).thenReturn(false);
    when(sellerRepository.save(any(Seller.class)))
        .thenAnswer(
            invocation -> {
              Seller seller = invocation.getArgument(0);
              seller.setId(20L);
              return seller;
            });

    service.registerSeller(10L, request);

    ArgumentCaptor<Seller> sellerCaptor = ArgumentCaptor.forClass(Seller.class);
    verify(sellerRepository).save(sellerCaptor.capture());
    Seller savedSeller = sellerCaptor.getValue();
    assertThat(savedSeller.getName()).isEqualTo("테스트상사");
    assertThat(savedSeller.getBusinessNumber()).isEqualTo("123-45-67890");

    ArgumentCaptor<SellerMember> memberCaptor = ArgumentCaptor.forClass(SellerMember.class);
    verify(sellerMemberRepository).save(memberCaptor.capture());
    SellerMember member = memberCaptor.getValue();
    SellerMemberId memberId = member.getId();
    assertThat(memberId.getSellerId()).isEqualTo(20L);
    assertThat(memberId.getUserId()).isEqualTo(10L);
    assertThat(member.getRole()).isEqualTo("OWNER");

    verify(userRepository).save(user);
    assertThat(user.getIsSeller()).isTrue();
  }

  @Test
  void registerSeller_throwsWhenAlreadyMember() {
    when(userRepository.findById(10L)).thenReturn(Optional.of(user));
    when(sellerMemberRepository.existsByIdUserId(10L)).thenReturn(true);

    IllegalStateException ex =
        assertThrows(IllegalStateException.class, () -> service.registerSeller(10L, request));
    assertThat(ex.getMessage()).contains("이미 판매자");

    verify(sellerRepository, never()).save(any());
  }

  @Test
  void registerSeller_throwsWhenBusinessNumberExists() {
    when(userRepository.findById(10L)).thenReturn(Optional.of(user));
    when(sellerMemberRepository.existsByIdUserId(10L)).thenReturn(false);
    when(sellerRepository.existsByBusinessNumber("123-45-67890")).thenReturn(true);

    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> service.registerSeller(10L, request));
    assertThat(ex.getMessage()).contains("이미 등록된 사업자 번호");

    verify(sellerMemberRepository, never()).save(any());
  }
}
