package com.example.java;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Context loading test is disabled until a test database is available")
class JavaApplicationTests {

  @Test
  void contextLoads() {}
}
