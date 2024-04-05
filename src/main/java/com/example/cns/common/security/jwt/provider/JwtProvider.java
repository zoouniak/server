package com.example.cns.common.security.jwt.provider;

import com.example.cns.auth.domain.RefreshToken;
import com.example.cns.auth.dto.response.AuthTokens;
import com.example.cns.auth.service.RefreshTokenService;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.security.exception.AuthException;
import com.example.cns.common.security.jwt.dto.JwtMemberInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {
    private final Key secretKey;
    private final Long accessExpirationTime;
    private final Long refreshExpirationTime;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final RefreshTokenService refreshTokenService;

    public JwtProvider(
            @Value("${security.jwt.secret-key}") String secretKey,
            @Value("${security.jwt.access-expiration-time}") Long accessExpirationTime,
            @Value("${security.jwt.refresh-expiration-time}") Long refreshExpirationTime,
            RefreshTokenService refreshTokenService
    ) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthTokens generateLoginToken(JwtMemberInfo userInfo) {
        final String accessToken = createToken(userInfo, accessExpirationTime);
        final String refreshToken = createToken(userInfo, refreshExpirationTime);


        refreshTokenService.saveRefreshToken(getRefreshTokenEntity(userInfo, refreshToken));
        return new AuthTokens(accessToken, refreshToken);
    }

    public String createToken(JwtMemberInfo userInfo, Long tokenValidTime) {
        final Date now = new Date();

        return Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createClaims(userInfo))
                .signWith(secretKey, signatureAlgorithm)
                .setExpiration(createExpireDate(now, tokenValidTime))
                .compact();
    }

    public String getUserNameFromToken(String accessToken) {
        Claims claims = getClaims(accessToken);

        return claims.get("userId").toString();
    }

    public String resolveAccessToken(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return "";
    }

    public boolean isTokenExpired(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration().before(new Date());
    }

    private Map<String, Object> createHeader() {
        return Map.of("alg", "HS256", "typ", "jwt");
    }

    private Map<String, Object> createClaims(JwtMemberInfo userInfo) {
        return Map.of("userId", userInfo.memberId(), "role", userInfo.role());
    }

    private Date createExpireDate(Date now, Long expirationTime) {
        return new Date(now.getTime() + expirationTime);
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (MalformedJwtException e) {
            throw new AuthException(ExceptionCode.MALFORMED_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new AuthException(ExceptionCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new AuthException(ExceptionCode.INVALID_TOKEN);
        }
    }

    private RefreshToken getRefreshTokenEntity(JwtMemberInfo userInfo, String refreshToken) {
        return new RefreshToken(refreshToken, userInfo);
    }
}
