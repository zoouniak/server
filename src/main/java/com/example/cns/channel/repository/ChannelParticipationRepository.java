package com.example.cns.channel.repository;

import com.example.cns.channel.domain.ChannelParticipation;
import com.example.cns.channel.domain.ChannelParticipationID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelParticipationRepository extends JpaRepository<ChannelParticipation, ChannelParticipationID> {

    @Query("SELECT c FROM ChannelParticipation c WHERE c.channel = :channelId")
    List<ChannelParticipation> findChannelParticipationByChannel(@Param("channelId") Long channelId);

    @Modifying
    @Query("DELETE FROM ChannelParticipation cp WHERE cp.channel = :channelId AND cp.member = :memberId")
    void deleteChannelParticipationByChannelAndMember(@Param("channelId") Long channelId, @Param("memberId") Long memberId);
}
