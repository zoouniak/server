package com.example.cns.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "verificationNumber", timeToLive = 300)
public class AuthCode {
    @Id
    private String email;
    private String authCode;
}
