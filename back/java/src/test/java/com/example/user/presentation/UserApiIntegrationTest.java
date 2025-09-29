package com.example.user.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.security.RefreshTokenStore;
import com.example.user.domain.UserRepository;
import com.example.user.presentation.dto.request.LoginRequest;
import com.example.user.presentation.dto.request.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserApiIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;

  @MockBean private RefreshTokenStore refreshTokenStore;

  @BeforeEach
  void cleanBefore() {
    userRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
  }

  @Test
  void registerAndLoginFlow() throws Exception {
    RegisterRequest registerRequest = new RegisterRequest();
    registerRequest.setEmail("test@example.com");
    registerRequest.setName("테스터");
    registerRequest.setPassword("password123");
    registerRequest.setProvider("local");
    registerRequest.setIsSeller(false);

    mockMvc
        .perform(
            post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true));

    assertThat(userRepository.existsByEmailIgnoreCase("test@example.com")).isTrue();

    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("test@example.com");
    loginRequest.setPassword("password123");

    mockMvc
        .perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.token").isNotEmpty())
        .andExpect(jsonPath("$.refreshToken").isNotEmpty());
  }
}
