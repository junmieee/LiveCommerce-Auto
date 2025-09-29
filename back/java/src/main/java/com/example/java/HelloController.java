package com.example.java;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Root")
public class HelloController {
  @GetMapping("/")
  @Operation(summary = "루트 헬스/환영")
  public String hello() {
    return "hello from Java (money project)!a!!!";
  }
}
