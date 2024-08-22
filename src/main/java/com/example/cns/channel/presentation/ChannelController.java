package com.example.cns.channel.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.channel.dto.request.ChannelRequest;
import com.example.cns.channel.dto.response.ChannelListResponse;
import com.example.cns.channel.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "채널 API", description = "채널과 관련된 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChannelController {

    private final ChannelService channelService;

    //채널 생성
    @Operation(summary = "채널 생성 api", description = "사용자로부터 채널 명을 입력받고, 채널을 생성한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId",description = "JWT/사용자 id"),
                    @Parameter(name = "channelRequest", description = "채널명 추가 DTO"),
                    @Parameter(name = "projectId",description = "프로젝트 인덱스값")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채널 생성이 되면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping("/project/{projectId}/channel")
    public ResponseEntity<?> createChannel(@Auth Long memberId, @PathVariable Long projectId, @RequestBody ChannelRequest channelRequest){
        channelService.createChannel(memberId,projectId, channelRequest);
        return ResponseEntity.ok().build();
    }

    //채널 삭제
    @Operation(summary = "채널 삭제 api", description = "사용자로부터 프로젝트 인덱스, 채널 인덱스를 받아와 채널을 삭제한다. 단, 사용자가 참여중이면 삭제가 불가능하다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "projectId", description = "프로젝트 인덱스 값"),
                    @Parameter(name = "channelId", description = "채널 인덱스 값"),
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채널이 삭제가 되면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @DeleteMapping("/project/{projectId}/channel/{channelId}")
    public ResponseEntity<?> deleteChannel(@Auth Long memberId, @PathVariable Long projectId, @PathVariable Long channelId){
        channelService.deleteChannel(memberId, projectId, channelId);
        return ResponseEntity.ok().build();
    }

    //채널 입장 1(세션 생성)
    @Operation(summary = "채널 세션 생성 api", description = "사용자로부터 프로젝트 인덱스값, 채널 인덱스값을 받아와 세션을 생성한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "projectId", description = "프로젝트 인덱스 값"),
                    @Parameter(name = "channelId", description = "채널 인덱스 값"),
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채널에 대한 세션을 만들어 사용자에게 전달한다. 해당 세션은 토큰 생성에 사용된다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping("/project/{projectId}/channel/{channelId}")
    public ResponseEntity<?> enterChannel(@Auth Long memberId,@PathVariable Long projectId, @PathVariable Long channelId){
        return ResponseEntity.ok(channelService.createSession(memberId,projectId,channelId));
    }

    //채널 입장 2(토큰 생성)
    @Operation(summary = "채널 토큰 생성 api", description = "사용자로부터 프로젝트 인덱스, 채널 인덱스, 세션을 받아와 토큰을 생성해준다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "projectId", description = "프로젝트 인덱스 값"),
                    @Parameter(name = "channelId",description = "채널 인덱스 값"),
                    @Parameter(name = "sessionId", description = "채널 세션 값")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "화상채팅을 할수 있는 토큰을 발급받는다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping("/project/{projectId}/channel/{channelId}/connection/{sessionId}")
    public ResponseEntity<?> createToken(@Auth Long memberId,@PathVariable Long projectId, @PathVariable Long channelId, @PathVariable String sessionId){
        return ResponseEntity.ok(channelService.createToken(memberId,projectId,channelId, sessionId));
    }

    //채널 퇴장
    @Operation(summary = "채널 퇴장 api", description = "사용자로부터 프로젝트 인덱스, 채널 인덱스를 받고 채널을 나간다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "projectId", description = "프로젝트 인덱스 값"),
                    @Parameter(name = "channelId",description = "채널 인덱스 값")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채널을 나가는게 성공하면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @DeleteMapping("/project/{projectId}/channel/{channelId}/connection")
    public ResponseEntity<?> exitChannel(@Auth Long memberId, @PathVariable Long projectId, @PathVariable Long channelId){
        channelService.exitChannel(memberId,projectId,channelId);
        return ResponseEntity.ok().build();
    }

    //채널 조회
    @Operation(summary = "채널 조회 api", description = "사용자로부터 프로젝트 인덱스를 받아 프로젝트내 채널들을 조회한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "projectId", description = "프로젝트 인덱스 값")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 프로젝트의 채널 목록과 참여자 정보를 반환한다.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelListResponse.class))))
    })
    @GetMapping("/project/{projectId}/channel")
    public ResponseEntity<?> getChannelList(@Auth Long memberId, @PathVariable Long projectId){
        return ResponseEntity.ok(channelService.getChannelList(memberId,projectId));
    }

    //채널명 수정
    @Operation(summary = "채널명 수정 api", description = "사용자로부터 프로젝트 인덱스, 채널 인덱스, 채널명을 받아와 채널명을 수정한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "projectId", description = "프로젝트 인덱스 값"),
                    @Parameter(name = "channelId",description = "채널 인덱스 값"),
                    @Parameter(name = "channelRequest",description = "채널명 수정 DTO")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채널명을 바꾸는데 성공하면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PutMapping("/project/{projectId}/channel/{channelId}")
    public ResponseEntity<?> updateChannelName(@Auth Long memberId,@PathVariable Long projectId, @PathVariable Long channelId,@RequestBody ChannelRequest channelRequest){
        channelService.updateChannelName(memberId,projectId,channelId, channelRequest);
        return ResponseEntity.ok().build();
    }
}
