package com.example.cns.chat.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.chat.dto.ChatRoomCreateRequest;
import com.example.cns.chat.dto.ChatRoomIdResponse;
import com.example.cns.chat.dto.ChatRoomResponse;
import com.example.cns.chat.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    /*
     * 사용자가 참여하고 있는 채팅방 목록 조회
     */
    @Operation(summary = "사용자가 참여하고 있는 채팅방 목록 조회", description = "로그인한 사용자가 참여 중인 채팅방 목록을 조회하낟.")
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
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity createChatRoom(@Auth Long memberId, @RequestBody ChatRoomCreateRequest request) {
        //채팅방 저장
        Long chatRoom = chatRoomService.createChatRoom(request, memberId);
        return ResponseEntity.ok(new ChatRoomIdResponse(chatRoom));
    }
}
