package com.example.cns.mention.domain.repository;

import com.example.cns.mention.domain.Mention;
import com.example.cns.mention.type.MentionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MentionRepository extends JpaRepository<Mention, Long> {

    @Modifying
    @Query("DELETE FROM Mention m where m.mentionType= :type and m.subjectId= :postId")
    void deleteAllMentionBySubjectId(@Param("postId") Long postId, @Param("type") MentionType mentionType);

    @Modifying
    @Query("DELETE FROM Mention m where m.mentionType = :type and m.subjectId = :postId and m.member.id = :memberId")
    void deleteMentionBySubjectIdAndMentionTypeAndMember(@Param("postId") Long postId, @Param("type") MentionType mentionType, @Param("memberId") Long memberId);

    @Query("SELECT m.subjectId, m.member.id FROM Mention m WHERE m.subjectId IN :subjectIds AND m.mentionType = :mentionType")
    List<Object[]> findMentionsBySubjectId(@Param("subjectIds") List<Long> subjectIds, @Param("mentionType") MentionType mentionType);
}
