package com.example.java;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Root")
public class HelloController {
  @GetMapping("/")
  @Operation(summary = "루트 헬스/환영")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "서비스 헬스 문자열",
        content = @Content(schema = @Schema(implementation = String.class)))
  })
  public String hello() {
    return "hello from Java (money project)!a!!!";
  }
}
