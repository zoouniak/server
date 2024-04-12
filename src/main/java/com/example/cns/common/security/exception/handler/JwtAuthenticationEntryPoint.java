package com.example.cns.common.security.exception.handler;

import com.example.cns.common.exception.ExceptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 유저 정보가 없는 경우 : 401 Unauthorized
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ExceptionResponse exceptionResponse = new ExceptionResponse(4001, "인증되지 않은 사용자입니다.");
        response.setStatus(403);
        objectMapper.writeValue(response.getWriter(), exceptionResponse);
    }
}