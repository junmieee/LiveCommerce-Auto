package com.example.product.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.product.domain.Product;
import com.example.product.domain.ProductRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductApiIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ProductRepository productRepository;

  @BeforeEach
  void setUp() {
    productRepository.deleteAll();
  }

  @Test
  void publicProductListReturnsActiveProducts() throws Exception {
    productRepository.save(
        Product.builder()
            .sellerId(1L)
            .name("테스트상품")
            .description("설명")
            .price(new BigDecimal("9900"))
            .stockQuantity(100)
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build());

    mockMvc
        .perform(get("/api/products").param("page", "1").param("limit", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data[0].name").value("테스트상품"));
  }
}
