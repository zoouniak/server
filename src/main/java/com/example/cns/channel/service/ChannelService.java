package com.example.cns.channel.service;

import com.example.cns.channel.domain.Channel;
import com.example.cns.channel.domain.ChannelParticipation;
import com.example.cns.channel.domain.ChannelParticipationID;
import com.example.cns.channel.dto.request.ChannelRequest;
import com.example.cns.channel.dto.response.ChannelListResponse;
import com.example.cns.channel.repository.ChannelParticipationRepository;
import com.example.cns.channel.repository.ChannelRepository;
import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.feed.post.dto.response.PostMember;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.project.domain.Project;
import com.example.cns.project.domain.ProjectParticipation;
import com.example.cns.project.domain.ProjectParticipationID;
import com.example.cns.project.domain.repository.ProjectParticipationRepository;
import com.example.cns.project.domain.repository.ProjectRepository;
import io.openvidu.java.client.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChannelService {

    private OpenVidu openVidu;

    @Value("${external-api.openvidu-url}")
    private String openViduURL;

    @Value("${external-api.openvidu-url-secret}")
    private String openViduSecret;

    private final ChannelRepository channelRepository;
    private final ProjectParticipationRepository projectParticipationRepository;
    private final ChannelParticipationRepository channelParticipationRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    @PostConstruct
    public void init(){this.openVidu = new OpenVidu(openViduURL,openViduSecret);}

    @Transactional
    public void createChannel(Long memberId, Long projectId, ChannelRequest channelRequest) {

        Optional<ProjectParticipation> projectParticipation = isProjectParticipation(memberId, projectId);

        if(projectParticipation.isPresent()){

            Optional<Project> project = projectRepository.findById(projectId);

            channelRepository.save(Channel.builder()
                            .name(channelRequest.name())
                            .project(project.get())
                    .build());
        } else {
            throw new BusinessException(ExceptionCode.FAIL_CREATE_CHANNEL);
        }
    }

    @Transactional
    public void deleteChannel(Long memberId, Long projectId, Long channelId) {
        Optional<ProjectParticipation> projectParticipation = isProjectParticipation(memberId, projectId);

        if(projectParticipation.isPresent()){
            Optional<Channel> channelExists = isChannelExists(projectId, channelId);

            if(channelExists.isPresent()){
                List<ChannelParticipation> participation = channelParticipationRepository.findChannelParticipationByChannel(channelId);
                if(participation.size() > 0){
                    throw new BusinessException(ExceptionCode.CHANNEL_PARTICIPATION_EXIST);
                } else channelRepository.deleteById(channelId);
            }
        }
    }

    public String createSession(Long memberId, Long projectId, Long channelId) {
        Optional<ProjectParticipation> projectParticipation = isProjectParticipation(memberId, projectId);

        if(projectParticipation.isPresent()){
            Optional<Channel> channel = isChannelExists(projectId, channelId);
            if(channel.isPresent()){
                SessionProperties sessionProperties = new SessionProperties.Builder()
                        .customSessionId(channel.get().getName()+channelId) //세션 아이디 고유성 + 재접근용
                        .build();
                try {
                    Session session =openVidu.createSession(sessionProperties);
                    return session.getSessionId();
                } catch (OpenViduJavaClientException | OpenViduHttpException e) {
                    throw new BusinessException(ExceptionCode.FAIL_GET_API);
                }
            } else throw new BusinessException(ExceptionCode.CHANNEL_NOT_EXIST);
        } else {
            throw new BusinessException(ExceptionCode.NOT_PROJECT_PARTICIPANTS);
        }
    }

    @Transactional
    public String createToken(Long memberId,Long projectId,Long channelId, String sessionId) {

        Member member = isMemberExists(memberId);

        try{
            Session session = openVidu.getActiveSession(sessionId);

            ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
                    .type(ConnectionType.WEBRTC)
                    .role(OpenViduRole.PUBLISHER)
                    .data(member.getNickname())
                    .build();

            Connection connection =session.createConnection(connectionProperties);

            enterChannel(projectId,memberId,channelId);

            return connection.getToken();
        }catch (OpenViduJavaClientException | OpenViduHttpException e){
            e.printStackTrace();
            throw new BusinessException(ExceptionCode.FAIL_GET_API);
        }
    }

    @Transactional
    public void exitChannel(Long memberId, Long projectId, Long channelId) {
        Optional<ChannelParticipation> channelParticipation = channelParticipationRepository.findById(new ChannelParticipationID(memberId, channelId));
        if(channelParticipation.isPresent()){
            channelParticipationRepository.deleteChannelParticipationByChannelAndMember(channelId,memberId);
        } else throw new BusinessException(ExceptionCode.NOT_CHANNEL_PARTICIPATION);
    }

    public List<ChannelListResponse> getChannelList(Long memberId, Long projectId) {

        Optional<ProjectParticipation> participation = isProjectParticipation(memberId, projectId);

        if(participation.isPresent()){
            List<Object[]> channels = channelRepository.findChannelsWithMemberInfoByProject(projectId);

            Map<Channel, List<PostMember>> channelWithMembersMap = new HashMap<>();

            for (Object[] result : channels) {
                Channel channel = (Channel) result[0];
                Member member = (Member) result[1];

                channelWithMembersMap.computeIfAbsent(channel, k -> new ArrayList<>());

                if(member != null){
                    channelWithMembersMap.get(channel).add(PostMember.builder()
                            .id(member.getId())
                            .nickname(member.getNickname())
                            .profile(member.getUrl())
                            .build());
                }
            }

            return channelWithMembersMap.entrySet().stream()
                    .map(entry -> ChannelListResponse.builder()
                            .id(entry.getKey().getId())
                            .name(entry.getKey().getName())
                            .members(entry.getValue())
                            .build())
                    .sorted(Comparator.comparingLong(ChannelListResponse::id))
                    .collect(Collectors.toList());
        } else throw new BusinessException(ExceptionCode.NOT_PROJECT_PARTICIPANTS);
    }

    @Transactional
    public void updateChannelName(Long memberId, Long projectId, Long channelId, ChannelRequest channelRequest) {
        Optional<ProjectParticipation> participation = isProjectParticipation(memberId, projectId);

        if(participation.isPresent()){
            Optional<Channel> channel = channelRepository.findChannelByChannelIdAndProjectId(channelId, projectId);
            if(channel.isPresent()){
                channel.get().updateChannelName(channelRequest.name());
                channelRepository.save(channel.get());
            } else throw new BusinessException(ExceptionCode.CHANNEL_NOT_EXIST);
        }else throw new BusinessException(ExceptionCode.NOT_PROJECT_PARTICIPANTS);
    }


    //method
    private void enterChannel(Long projectId,Long memberId, Long channelId){

        Optional<Channel> channelExists = isChannelExists(projectId, channelId);
        if(channelExists.isPresent()){
            channelParticipationRepository.save(ChannelParticipation.builder()
                    .member(memberId)
                    .channel(channelId)
                    .build());
        }
    }

    private Optional<ProjectParticipation> isProjectParticipation(Long memberId, Long projectId){
        return projectParticipationRepository.findById(ProjectParticipationID.builder()
                .project(projectId)
                .member(memberId)
                .build());
    }

    private Optional<Channel> isChannelExists(Long projectId, Long channelId){
        return channelRepository.findChannelByChannelIdAndProjectId(channelId,projectId);
    }

    private Member isMemberExists(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
