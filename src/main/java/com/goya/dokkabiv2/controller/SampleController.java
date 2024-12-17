package com.goya.dokkabiv2.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.goya.dokkabiv2.domain.QuestData;
import com.goya.dokkabiv2.domain.Role;
import com.goya.dokkabiv2.domain.User;
import com.goya.dokkabiv2.service.UserService;
import com.goya.dokkabiv2.util.GoogleSheetParser;
import com.goya.dokkabiv2.util.JWTToken;
import com.goya.dokkabiv2.util.JwtTokenProvider;
import com.goya.dokkabiv2.util.SheetToJsonConverter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.List;
import java.util.Optional;

@RestController
public class SampleController {
    private static final Logger logger = LoggerFactory.getLogger(SampleController.class);


    @Value("classpath:google_spread_sheet_key2.json") // classpath에서 파일 로드
    private Resource credentialsResource;

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @GetMapping("/test")
    public String test() {
        logger.error("Test Exception occurred");
        logger.info("INFO 테스트 메시지");
        throw new RuntimeException("Test Exception");
    }

    @GetMapping("/test2")
    public String test2() {
        try {
            // 리소스에서 InputStream 가져오기
            InputStream inputStream = credentialsResource.getInputStream();

            // GoogleCredential 객체 생성
            GoogleCredential credential = GoogleCredential.fromStream(inputStream);

            // Google Sheets 데이터 가져오기
            GoogleSheetParser parser = new GoogleSheetParser(credential);
            String spreadsheetId = "your-spreadsheet-id"; // 스프레드시트 ID
            String sheetName = "Quest"; // 시트 이름
            List<List<Object>> sheetData = parser.getSheetData(spreadsheetId, sheetName);

            // 데이터를 Quest 객체로 변환
            List<QuestData> questDataList = SheetToJsonConverter.parseSheetData(QuestData.class, sheetData);

            // 결과 출력 및 JSON 저장
            questDataList.forEach(System.out::println);
            SheetToJsonConverter.saveAsJson(questDataList, "quest_data.json");
            System.out.println("JSON 파일로 저장 완료!");

        } catch (IOException e) {
            System.err.println("서비스 계정 인증 정보를 로드하는 동안 오류 발생: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "success";
    }

    @GetMapping("/test-schema")
    public String testSchema() {
        String query = "SELECT name FROM test_table WHERE id = 1";
        return jdbcTemplate.queryForObject(query, String.class);
    }

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/token")
    public ResponseEntity<JWTToken> generateToken(OAuth2AuthenticationToken authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().build();
        }

        OAuth2User principal = authentication.getPrincipal();
        String email = principal.getAttribute("email");  // 구글 계정의 이메일 정보 가져오기
        String name = principal.getAttribute("name");    // 구글 계정의 이름 가져오기
        String picture = principal.getAttribute("picture");    // 구글 계정의 프로필 사진 가져오기

        // 이메일을 기반으로 사용자 정보 조회
        Optional<User> user = userService.findByUsername(email);

        // 사용자 정보가 없다면 새로 생성
        user.orElseGet(() -> userService.save(new User(name, email, picture, Role.USER)));

        // JwtTokenProvider를 사용해 JWTToken 생성
        JWTToken jwtToken = jwtTokenProvider.generateToken(authentication);

        // 생성된 JWTToken을 응답으로 반환
        return ResponseEntity.ok(jwtToken);
    }
}
