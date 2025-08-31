package com.example.java;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
  @GetMapping("/")
  public String hello() {
    return "hello from Java (money project)!a!!!";
  }
}
