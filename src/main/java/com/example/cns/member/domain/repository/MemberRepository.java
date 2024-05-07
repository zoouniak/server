package com.example.cns.member.domain.repository;

import com.example.cns.company.domain.Company;
import com.example.cns.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    List<Member> findAllByNicknameContaining(String nickname);

    List<Member> findAllByNicknameContainingAndCompany(String nickname, Company company);
}
