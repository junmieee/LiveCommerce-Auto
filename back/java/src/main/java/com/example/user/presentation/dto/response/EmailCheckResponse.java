package com.example.user.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailCheckResponse {
  private boolean exists;
}
