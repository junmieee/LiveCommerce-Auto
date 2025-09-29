package com.example.product.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.product.domain.Product;
import com.example.product.domain.ProductRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
            .descriptionHtml("<p>설명</p>")
            .price(new BigDecimal("9900"))
            .stockQuantity(100)
            .thumbnailImageKey("products/1/thumb.jpg")
            .mainImageKeys(List.of("products/1/main-1.jpg", "products/1/main-2.jpg"))
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
