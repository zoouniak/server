package com.example.cns.chat.domain;

import com.example.cns.common.FileEntity;
import com.example.cns.common.type.FileType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "message_file")
@Entity
@Getter
@NoArgsConstructor
public class ChatFile extends FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Builder
    public ChatFile(Chat chat, String url, FileType fileType, String fileName, LocalDateTime createdAt) {
        this.setUrl(url);
        this.setFileName(fileName);
        this.setCreatedAt(createdAt);
        this.setFileType(fileType);
        this.chat = chat;
    }
}
