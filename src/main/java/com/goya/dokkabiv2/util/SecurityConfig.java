package com.goya.dokkabiv2.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public CorsFilter corsFilter() {
        // CORS 설정
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // 허용할 Origin
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // 허용할 HTTP 메서드
        corsConfiguration.addAllowedHeader("*"); // 허용할 헤더
        corsConfiguration.setAllowCredentials(true); // 자격 증명 허용

        // CORS 정책을 경로에 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);  // CORS 필터를 반환
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화를 위한 Bean
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .requiresChannel(channel ->
                        channel.anyRequest().requiresSecure() // 모든 요청을 HTTPS로 강제
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api-docs/**").permitAll() // Swagger 접근 허용
                        .requestMatchers("/test").permitAll() // Swagger 접근 허용
                        .requestMatchers("/token").permitAll() // 토큰 생성 API 접근 허용
                        .anyRequest().authenticated() // 다른 요청은 인증 필요
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가
                .oauth2Login(oauth2 -> oauth2
                        //.loginPage("/login") // 사용자 정의 로그인 페이지
                        .defaultSuccessUrl("/test-schema", true) // 로그인 성공 후 리디렉션될 URL
                );

        return http.build();
    }
}
