package com.example.cns.chat.domain.repository;

import com.example.cns.chat.domain.ChatParticipation;
import com.example.cns.chat.domain.ChatParticipationID;
import com.example.cns.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipationRepository extends JpaRepository<ChatParticipation, ChatParticipationID> {
    Optional<ChatParticipation> findById(ChatParticipationID id);

    void deleteByMemberAndRoom(Long member, Long room);

    @Query("select m from Member m inner join ChatParticipation cp on m.id = cp.member and cp.room=:room")
    List<Member> findMemberByRoom(Long room);
}
