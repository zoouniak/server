package com.example.cns.chat.service;

import com.example.cns.chat.domain.Chat;
import com.example.cns.chat.domain.ChatFile;
import com.example.cns.chat.domain.ChatFileRepository;
import com.example.cns.chat.domain.ChatRoom;
import com.example.cns.chat.domain.repository.ChatListRepositoryImpl;
import com.example.cns.chat.domain.repository.ChatRepository;
import com.example.cns.chat.domain.repository.ChatRoomRepository;
import com.example.cns.chat.dto.request.FileMessageFormat;
import com.example.cns.chat.dto.request.TextMessageFormat;
import com.example.cns.chat.dto.response.ChatFileResponse;
import com.example.cns.chat.dto.response.ChatResponse;
import com.example.cns.chat.type.MessageType;
import com.example.cns.common.exception.BusinessException;
import com.example.cns.feed.post.dto.response.FileResponse;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.cns.common.exception.ExceptionCode.CHATROOM_NOT_EXIST;
import static com.example.cns.common.exception.ExceptionCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatListRepositoryImpl chatListRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatFileRepository chatFileRepository;
    private final MemberRepository memberRepository;

    /*
     * 채팅(텍스트 타입) 저장
     */
    @Transactional
    public Long saveTextMessage(TextMessageFormat message) {
        Member sender = getMember(message.memberId());
        ChatRoom chatRoom = getChatRoom(message.roomId());

        // 채팅 저장
        Chat save = chatRepository.save(message.toChatEntity(chatRoom, sender));

        return save.getId();
    }

    private ChatRoom getChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId).orElseThrow(() -> new BusinessException(CHATROOM_NOT_EXIST));
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
    }

    /*
     * 채팅(파일 타입) 저장
     */
    @Transactional
    public Long saveFileMessage(FileMessageFormat fileMessage, FileResponse fileInfo) {
        Member sender = getMember(fileMessage.memberId());
        ChatRoom chatRoom = getChatRoom(fileMessage.roomId());

        // 채팅 저장
        Chat save = chatRepository.save(Chat.builder().chatRoom(chatRoom)
                .from(sender)
                .content(fileInfo.uploadFileURL())
                .messageType(fileMessage.messageType())
                .createdAt(fileMessage.createdAt())
                .subjectId(null)
                .build());

        // 채팅 파일 저장
        ChatFile chatFile = ChatFile.builder().fileName(fileInfo.uploadFileName())
                .url(fileInfo.uploadFileURL())
                .fileType(fileInfo.fileType())
                .createdAt(fileMessage.createdAt())
                .chat(save)
                .build();

        chatFileRepository.save(chatFile);

        return save.getId();
    }

    /*
     * 채팅 내역 페이지네이션
     */
    @Transactional(readOnly = true)
    public List<ChatResponse> getPaginationChat(Long roomId, Long chatId) {
        // 스크롤에 따라 no offset 페이징
        return chatListRepository.paginationChat(roomId, chatId, 10);
    }

    public List<ChatFileResponse> getImages(Long roomId) {
        ChatRoom chatRoom = getChatRoom(roomId);
        List<Chat> allFiles = chatRepository.getAllByChatRoomAndMessageType(chatRoom, MessageType.IMAGE);
        return allFiles.stream().map(chat -> new ChatFileResponse(chat.getContent(), chat.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
