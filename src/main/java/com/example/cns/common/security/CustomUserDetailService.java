package com.example.cns.common.security;

import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.security.exception.AuthException;
import com.example.cns.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {

        return memberRepository.findById(Long.valueOf(memberId))
                .map(CustomUserDetails::of)
                .orElseThrow(() -> new AuthException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
