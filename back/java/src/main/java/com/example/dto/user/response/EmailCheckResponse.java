package com.example.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailCheckResponse {
    private boolean exists;
}