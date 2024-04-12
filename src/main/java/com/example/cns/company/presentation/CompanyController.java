package com.example.cns.company.presentation;

import com.example.cns.auth.dto.response.CompanyEmailResponse;
import com.example.cns.common.exception.ExceptionResponse;
import com.example.cns.company.dto.CompanySearchResponse;
import com.example.cns.company.service.CompanySearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "회사 API", description = "회사와 관련된 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/company")
public class CompanyController {
    private final CompanySearchService companySearchService;

    @Operation(summary = "키워드로 회사를 검색하는 api", description = "사용자에게 키워드를 입력받고 키워드가 포함된 모든 회사를 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "키워드가 포함된 회사를 리스트 형태로 반환한다.",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = CompanySearchResponse.class)
                            ))),
            @ApiResponse(responseCode = "204", description = "키워드가 포함된 회사가 없다.")
    })
    @Parameter(name = "keyword", description = "검색할 키워드")
    @PreAuthorize("isAnonymous()")
    @GetMapping("/search")
    public ResponseEntity<?> searchCompany(@RequestParam(name = "keyword") String keyword) {
        System.out.println(keyword);
        List<CompanySearchResponse> companyRes = companySearchService.searchCompanyByName(keyword);

        if (companyRes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(companyRes);
    }

    @Operation(summary = "회사 이메일 요청 api", description = "사용자가 선택한 회사 이름을 요청 받아 회사 이메일을 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "해당 회사가 존재하지 않는다.",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ExceptionResponse.class)
                            ))
            ),
            @ApiResponse(responseCode = "200", description = "회사 이름과 이메일을 반환한다.",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = CompanyEmailResponse.class)
                            )))
    })
    @Parameter(name = "companyName", description = "회사 이름")
    @PreAuthorize("isAnonymous()")
    @GetMapping("/get-email/{companyName}")
    public ResponseEntity<?> getCompanyEmail(@PathVariable(name = "companyName") String companyName) {
        return ResponseEntity.ok(companySearchService.getCompanyEmail(companyName));
    }

}
