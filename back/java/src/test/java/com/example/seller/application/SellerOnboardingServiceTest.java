package com.example.seller.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.seller.application.command.RegisterSellerCommand;
import com.example.seller.application.command.RegisterSellerWithUserCommand;
import com.example.seller.domain.Seller;
import com.example.seller.domain.SellerMember;
import com.example.seller.domain.SellerMemberId;
import com.example.seller.domain.SellerMembershipRepository;
import com.example.seller.domain.SellerRepository;
import com.example.user.domain.User;
import com.example.user.domain.UserRepository;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class SellerOnboardingServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private SellerRepository sellerRepository;
  @Mock private SellerMembershipRepository sellerMembershipRepository;
  @Mock private PasswordEncoder passwordEncoder;

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

  @Test
  void registerSellerWithNewUser_createsUserAndSeller() {
    RegisterSellerWithUserCommand newUserCommand =
        RegisterSellerWithUserCommand.builder()
            .email("OWNER@TEST.COM")
            .name("홍길동")
            .password("password123")
            .companyName("테스트상사")
            .businessNumber("123-45-67890")
            .contactPhone("010-1234-5678")
            .settlementCycle("monthly")
            .payoutDay((short) 5)
            .build();

    when(userRepository.existsByEmailIgnoreCase("owner@test.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encoded");

    AtomicReference<User> savedUserRef = new AtomicReference<>();
    when(userRepository.save(any(User.class)))
        .thenAnswer(
            invocation -> {
              User toSave = invocation.getArgument(0);
              if (toSave.getId() == null) {
                toSave.setId(30L);
              }
              savedUserRef.set(toSave);
              return toSave;
            });

    when(sellerMembershipRepository.existsByUserId(30L)).thenReturn(false);
    when(sellerRepository.existsByBusinessNumber("123-45-67890")).thenReturn(false);
    when(sellerRepository.save(any(Seller.class)))
        .thenAnswer(
            invocation -> {
              Seller seller = invocation.getArgument(0);
              seller.setId(40L);
              return seller;
            });

    service.registerSellerWithNewUser(newUserCommand);

    verify(passwordEncoder).encode("password123");
    User savedUser = savedUserRef.get();
    assertThat(savedUser).isNotNull();
    assertThat(savedUser.getEmail()).isEqualTo("owner@test.com");
    assertThat(savedUser.getIsSeller()).isTrue();

    verify(sellerRepository).save(any(Seller.class));
    verify(sellerMembershipRepository).save(any(SellerMember.class));
    verify(userRepository, times(2)).save(any(User.class));
  }

  @Test
  void registerSellerWithNewUser_throwsWhenEmailExists() {
    RegisterSellerWithUserCommand newUserCommand =
        RegisterSellerWithUserCommand.builder()
            .email("owner@test.com")
            .name("홍길동")
            .password("password123")
            .companyName("테스트상사")
            .businessNumber("123-45-67890")
            .build();

    when(userRepository.existsByEmailIgnoreCase("owner@test.com")).thenReturn(true);

    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () -> service.registerSellerWithNewUser(newUserCommand));
    assertThat(ex.getMessage()).contains("이미 존재하는 이메일");

    verify(userRepository, never()).save(any());
  }
}
