package com.goya.dokkabiv2.controller;

import com.goya.dokkabiv2.docs.SampleControllerDocs;
import com.goya.dokkabiv2.domain.Role;
import com.goya.dokkabiv2.domain.User;
import com.goya.dokkabiv2.service.UserService;
import com.goya.dokkabiv2.util.GoogleSheetToJsonConverter;
import com.goya.dokkabiv2.util.JWTToken;
import com.goya.dokkabiv2.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
public class SampleController implements SampleControllerDocs {
    private static final Logger logger = LoggerFactory.getLogger(SampleController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private GoogleSheetToJsonConverter googleSheetToJsonConverter;

    @Override
    @GetMapping("/data-patch")
    public ResponseEntity<String> dataPatch() {
        try {
            googleSheetToJsonConverter.dataPatch();
            return ResponseEntity.ok("데이터 패치가 완료되었습니다😄");
        } catch (Exception e) {
            logger.error("Error loading data", e);
            return ResponseEntity.status(500).body("데이터 패치 중 오류 발생😫");
        }
    }

    @Override
    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("health check");
    }

    @Hidden
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.error("Test Exception occurred");
        logger.info("INFO 테스트 메시지");
        return ResponseEntity.ok("Test endpoint working");
    }

    @Hidden
    @GetMapping("/test-schema")
    public String testSchema() {
        String query = "SELECT name FROM test_table WHERE id = 1";
        return jdbcTemplate.queryForObject(query, String.class);
    }

    @Hidden
    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @Hidden
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
