package com.example.cns.chat.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.chat.dto.request.ChatRoomCreateRequest;
import com.example.cns.chat.dto.response.ChatResponse;
import com.example.cns.chat.dto.response.ChatRoomIdResponse;
import com.example.cns.chat.dto.response.ChatRoomResponse;
import com.example.cns.chat.service.ChatRoomService;
import com.example.cns.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "채팅 api", description = "채팅방과 관련된 api")
@RestController
@RequestMapping("/api/v1/chat-room")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    /*
     * 사용자가 참여하고 있는 채팅방 목록 조회
     */
    @Operation(summary = "사용자가 참여하고 있는 채팅방 목록 조회", description = "로그인한 사용자가 참여 중인 채팅방 목록을 조회한다.")
    @Parameter(name = "memberId", description = "사용자 pk", required = true)
    @ApiResponse(responseCode = "200", description = "채팅방 목록 조회성공",
            content = @Content(schema = @Schema(implementation = ChatRoomResponse.class)))
    @GetMapping("/index")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getChatRoomPaging(@Auth Long memberId, @RequestParam(name = "page", defaultValue = "1") int page) {
        List<ChatRoomResponse> allChatroom = chatRoomService.findChatRoomsByPage(memberId, page);

        if (allChatroom == null) // 조회된 채팅방이 없는 경우
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(allChatroom);
    }

    /*
     * 채팅방 신규 개설
     */
    @Operation(summary = "채팅방 신규 개설", description = "채팅방 정보를 입력받고 새로운 채팅방을 생성한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "생성 완료"),
                    @ApiResponse(responseCode = "500", description = "생성 실패"
                    )
            })
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity createChatRoom(@Auth Long memberId, @RequestBody ChatRoomCreateRequest request) {
        //채팅방 저장
        Long chatRoom = chatRoomService.createChatRoom(request, memberId);
        return ResponseEntity.ok(new ChatRoomIdResponse(chatRoom));
    }

    @Operation(summary = "채팅방 나가기", description = "채팅방 번호를 입력 받고 채팅방 참여 정보를 삭제한다.")
    @Parameter(name = "roomId", description = "나갈 채팅방 번호, path variable", required = true)
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "나가기 완료"),
                    @ApiResponse(responseCode = "500", description = "나가기 실패"
                    )
            })
    @DeleteMapping("/{roomId}")
    public ResponseEntity leaveChatRoom(@Auth Long memberId, @PathVariable(name = "roomId") Long roomId) {
        chatRoomService.leaveChatRoom(memberId, roomId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅 내역 조회", description = "채팅방 번호를 입력 받고 해당 채팅방의 채팅 내역을 반환한다." +
            "chatId 파라미터를 전송하면 해당 채팅 이전 채팅들을 조회한다.(스크롤 페이징)")
    @Parameters({
            @Parameter(name = "roomId", description = "채팅방 번호", required = true),
            @Parameter(name = "chatId", description = "스크롤 페이징을 위한 채팅 번호")
    })
    @ApiResponse(responseCode = "200", description = "조회된 채팅 내역",
            content = @Content(schema = @Schema(implementation = ChatResponse.class)))
    @GetMapping("/{roomId}")
    public ResponseEntity getChatList(@PathVariable(name = "roomId") Long roomId, @RequestParam(name = "chatId", required = false) Long chatId) {
        List<ChatResponse> chat = chatService.getPaginationChat(roomId, chatId);
        return ResponseEntity.ok(chat);
    }

   /* @GetMapping("/{roomId}/participant")
    public ResponseEntity getParticipants(@PathVariable(name = "roomId")Long roomId) {
        chatRoomService.getChatParticipants(roomId);
    }*/

}
