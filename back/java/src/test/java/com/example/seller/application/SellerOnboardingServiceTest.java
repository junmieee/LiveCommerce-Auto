package com.example.seller.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.seller.application.command.RegisterSellerCommand;
import com.example.seller.domain.Seller;
import com.example.seller.domain.SellerMember;
import com.example.seller.domain.SellerMemberId;
import com.example.seller.domain.SellerMembershipRepository;
import com.example.seller.domain.SellerRepository;
import com.example.user.domain.User;
import com.example.user.domain.UserRepository;
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
  @Mock private SellerMembershipRepository sellerMembershipRepository;

  @InjectMocks private SellerOnboardingService service;

  private RegisterSellerCommand command;
  private User user;

  @BeforeEach
  void setUp() {
    command =
        RegisterSellerCommand.builder()
            .userId(10L)
            .companyName("테스트상사")
            .businessNumber("123-45-67890")
            .contactEmail("seller@test.com")
            .contactPhone("010-1234-5678")
            .settlementCycle("monthly")
            .payoutDay((short) 15)
            .build();

    user = User.builder().id(10L).email("user@test.com").isSeller(false).build();
  }

  @Test
  void registerSeller_createsSellerAndMemberAndMarksUser() {
    when(userRepository.findById(10L)).thenReturn(Optional.of(user));
    when(sellerMembershipRepository.existsByUserId(10L)).thenReturn(false);
    when(sellerRepository.existsByBusinessNumber("123-45-67890")).thenReturn(false);
    when(sellerRepository.save(any(Seller.class)))
        .thenAnswer(
            invocation -> {
              Seller seller = invocation.getArgument(0);
              seller.setId(20L);
              return seller;
            });

    service.registerSeller(command);

    ArgumentCaptor<Seller> sellerCaptor = ArgumentCaptor.forClass(Seller.class);
    verify(sellerRepository).save(sellerCaptor.capture());
    Seller savedSeller = sellerCaptor.getValue();
    assertThat(savedSeller.getName()).isEqualTo("테스트상사");
    assertThat(savedSeller.getBusinessNumber()).isEqualTo("123-45-67890");

    ArgumentCaptor<SellerMember> memberCaptor = ArgumentCaptor.forClass(SellerMember.class);
    verify(sellerMembershipRepository).save(memberCaptor.capture());
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
    when(sellerMembershipRepository.existsByUserId(10L)).thenReturn(true);

    IllegalStateException ex =
        assertThrows(IllegalStateException.class, () -> service.registerSeller(command));
    assertThat(ex.getMessage()).contains("이미 판매자");

    verify(sellerRepository, never()).save(any());
  }

  @Test
  void registerSeller_throwsWhenBusinessNumberExists() {
    when(userRepository.findById(10L)).thenReturn(Optional.of(user));
    when(sellerMembershipRepository.existsByUserId(10L)).thenReturn(false);
    when(sellerRepository.existsByBusinessNumber("123-45-67890")).thenReturn(true);

    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> service.registerSeller(command));
    assertThat(ex.getMessage()).contains("이미 등록된 사업자 번호");

    verify(sellerMembershipRepository, never()).save(any());
  }
}
