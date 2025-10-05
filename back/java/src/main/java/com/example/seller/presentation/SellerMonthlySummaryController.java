package com.example.seller.presentation;

import com.example.seller.application.SellerMonthlySummaryService;
import com.example.seller.domain.SellerMonthlySummary;
import com.example.user.presentation.dto.response.SimpleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sellermonthlysummarys")
@Tag(name = "Seller Monthly Summary")
@SecurityRequirement(name = "bearerAuth")
public class SellerMonthlySummaryController {
  private final SellerMonthlySummaryService service;

  public SellerMonthlySummaryController(SellerMonthlySummaryService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "요약 목록")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "판매자 월별 요약 목록",
        content =
            @Content(
                array =
                    @ArraySchema(schema = @Schema(implementation = SellerMonthlySummary.class)))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "403",
        description = "권한 부족",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public List<SellerMonthlySummary> getAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  @Operation(summary = "요약 상세")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "판매자 월별 요약",
        content = @Content(schema = @Schema(implementation = SellerMonthlySummary.class))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "403",
        description = "권한 부족",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "요약을 찾을 수 없음",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public SellerMonthlySummary getById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @Operation(summary = "요약 생성")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "요약 생성 완료",
        content = @Content(schema = @Schema(implementation = SellerMonthlySummary.class))),
    @ApiResponse(
        responseCode = "400",
        description = "검증 오류",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "403",
        description = "권한 부족",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public SellerMonthlySummary create(@RequestBody SellerMonthlySummary item) {
    return service.save(item);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "요약 삭제")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "삭제 완료"),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class))),
    @ApiResponse(
        responseCode = "403",
        description = "권한 부족",
        content = @Content(schema = @Schema(implementation = SimpleResponse.class)))
  })
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }
}
