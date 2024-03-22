package com.example.cns.auth.domain;

import com.example.cns.common.security.jwt.dto.JwtUserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 604800)
public class RefreshToken {
    @Id
    private String token;

    private JwtUserInfo userInfo;

}
