package com.example.cns.chat.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.chat.dto.ChatRoomCreateRequest;
import com.example.cns.chat.dto.ChatRoomIdResponse;
import com.example.cns.chat.service.ChatRoomService;
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
    @GetMapping("/index")
    public ResponseEntity getAllChatRoom(@Auth Long memberId) {
        System.out.println(memberId);
        List<String> allChatroom = chatRoomService.findAllChatroom(memberId);
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
