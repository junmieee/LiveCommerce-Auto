package com.example.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUser {
  private final Long id;
  private final String email;

  @Override
  public String toString() {
    return email != null ? email : String.valueOf(id);
  }
}
