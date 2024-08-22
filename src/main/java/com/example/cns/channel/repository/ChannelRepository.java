package com.example.cns.channel.repository;

import com.example.cns.channel.domain.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {

    @Query("SELECT c FROM Channel c WHERE c.id = :channelId AND c.project.id = :projectId")
    Optional<Channel> findChannelByChannelIdAndProjectId(@Param("channelId") Long channelId, @Param("projectId") Long projectId);

    @Query("SELECT c, m FROM Channel c LEFT JOIN ChannelParticipation cp ON c.id = cp.channel LEFT JOIN Member m ON cp.member = m.id WHERE c.project.id = :projectId")
    List<Object[]> findChannelsWithMemberInfoByProject(@Param("projectId") Long projectId);
}
