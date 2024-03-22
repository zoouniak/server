package com.example.cns.common.security.filter;

import com.example.cns.common.exception.ExceptionResponse;
import com.example.cns.common.security.CustomUserDetailService;
import com.example.cns.common.security.exception.AuthException;
import com.example.cns.common.security.jwt.provider.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.example.cns.common.exception.ExceptionCode.EMPTY_ACCESS_TOKEN;
import static com.example.cns.common.exception.ExceptionCode.EXPIRED_TOKEN;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final CustomUserDetailService userDetailService;

    private final List<String> ignoreUrls = List.of(
            "/api/v1/company/search",
            "/api/v1/company/get-email",
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/auth/check-duplicate",
            "/api/v1/email-auth",
            "/favicon.ico",
            "/api-docs",
            "/v3/api-docs",
            "/swagger-ui",
            "/css",
            "/images",
            "/js",
            "/swagger"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        System.out.println(requestURI);
        System.out.println(ignoreUrls.stream()
                .anyMatch(requestURI::startsWith));
        return ignoreUrls.stream()
                .anyMatch(requestURI::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = resolveToken(request);
            UserDetails userDetails = getUserDetails(accessToken);
            authenticateUser(userDetails, request);

            filterChain.doFilter(request, response);
        } catch (AuthException e) {
            handleAuthException(e, response);
        }

    }

    private String resolveToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");

        String token = jwtProvider.resolveAccessToken(authHeader);

        if (!StringUtils.hasText(token))
            throw new AuthException(EMPTY_ACCESS_TOKEN);

        if (jwtProvider.isTokenExpired(token))
            throw new AuthException(EXPIRED_TOKEN);

        return token;
    }

    private UserDetails getUserDetails(String accessToken) {
        String username = jwtProvider.getUserNameFromToken(accessToken);

        return userDetailService.loadUserByUsername(username);
    }

    private void handleAuthException(AuthException e, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getErrorCode(), e.getMessage());
        response.setStatus(e.getStatus().value());
        objectMapper.writeValue(response.getWriter(), exceptionResponse);
    }

    private void authenticateUser(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
