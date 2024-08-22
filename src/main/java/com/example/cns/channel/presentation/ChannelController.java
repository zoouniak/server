package com.example.cns.channel.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.channel.dto.request.ChannelCreateRequest;
import com.example.cns.channel.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChannelController {

    private final ChannelService channelService;

    //채널 생성
    @PostMapping("/project/{projectId}/channel")
    public ResponseEntity<?> createChannel(@Auth Long memberId, @PathVariable Long projectId, @RequestBody ChannelCreateRequest channelCreateRequest){
        channelService.createChannel(memberId,projectId,channelCreateRequest);
        return ResponseEntity.ok().build();
    }

    //채널 삭제
    @DeleteMapping("/project/{projectId}/channel/{channelId}")
    public ResponseEntity<?> deleteChannel(@Auth Long memberId, @PathVariable Long projectId, @PathVariable Long channelId){
        channelService.deleteChannel(memberId, projectId, channelId);
        return ResponseEntity.ok().build();
    }

    //채널 입장 1(세션 생성)
    @PostMapping("/project/{projectId}/channel/{channelId}")
    public ResponseEntity<?> enterChannel(@Auth Long memberId,@PathVariable Long projectId, @PathVariable Long channelId){
        return ResponseEntity.ok(channelService.createSession(memberId,projectId,channelId));
    }

    //채널 입장 2(토큰 생성)
    @PostMapping("/project/{projectId}/channel/{channelId}/connection/{sessionId}")
    public ResponseEntity<?> createToken(@Auth Long memberId,@PathVariable Long projectId, @PathVariable Long channelId, @PathVariable String sessionId){
        return ResponseEntity.ok(channelService.createToken(memberId,projectId,channelId, sessionId));
    }

    //채널 퇴장
    @DeleteMapping("/project/{projectId}/channel/{channelId}/connection")
    public ResponseEntity<?> exitChannel(@Auth Long memberId, @PathVariable Long projectId, @PathVariable Long channelId){
        channelService.exitChannel(memberId,projectId,channelId);
        return ResponseEntity.ok().build();
    }

    //채널 조회
    @GetMapping("/project/{projectId}/channel")
    public ResponseEntity<?> getChannelList(@Auth Long memberId, @PathVariable Long projectId){
        return ResponseEntity.ok(channelService.getChannelList(memberId,projectId));
    }

    //채널명 수정
    @PutMapping("/project/{projectId}/channel/{channelId}")
    public ResponseEntity<?> updateChannelName(@Auth Long memberId,@PathVariable Long projectId, @PathVariable Long channelId,@RequestBody ChannelCreateRequest channelCreateRequest){
        channelService.updateChannelName(memberId,projectId,channelId,channelCreateRequest);
        return ResponseEntity.ok().build();
    }
}
