package com.goya.dokkabiv2.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "테스트 API", description = "테스트 API 목록입니다.")
public interface SampleControllerDocs {
    @Operation(summary = "데이터 패치", description = "구글 시트에서 데이터를 파싱하여 서버에 적용합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "데이터 패치 완료")
    })
    public ResponseEntity<String> dataPatch();

    @Operation(summary = "서버 상태 확인", description = "서버가 요청을 처리할 수 있는지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "서버 정상 작동")
    })
    public ResponseEntity<String> healthCheck();

}