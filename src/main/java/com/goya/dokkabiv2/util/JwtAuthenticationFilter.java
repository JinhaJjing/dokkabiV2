package com.goya.dokkabiv2.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

/**
 * 요청 헤더의 JWT를 검증하고 인증 정보를 설정하는 필터
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 1. Request Header에서 JWT 토큰 추출
        String token = resolveToken(httpRequest);

        // 2. validateToken으로 토큰 유효성 검사
        if (token == null) {
            log.debug("JWT Token is missing from Authorization header. Request URI: {}", httpRequest.getRequestURI());
        } else if (!jwtTokenProvider.validateToken(token)) {
            log.debug("Invalid JWT Token received. Token: {} | Request URI: {}", token, httpRequest.getRequestURI());
        } else {
            // 토큰이 유효한 경우, SecurityContext에 인증 정보 설정
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("JWT Token successfully validated. User: {} | Request URI: {}", authentication.getName(), httpRequest.getRequestURI());
        }

        // 3. 필터 체인의 다음 필터로 요청 전달
        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}