package com.example.cns.member.domain.repository;

import com.example.cns.member.domain.MemberResume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberResumeRepository extends JpaRepository<MemberResume, Long> {
    @Query("SELECT mr FROM member_resume mr WHERE mr.member.id = :memberId")
    Optional<MemberResume> findByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM member_resume mr WHERE mr.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);
}
