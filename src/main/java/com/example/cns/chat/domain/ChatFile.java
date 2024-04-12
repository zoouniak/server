package com.example.cns.chat.domain;

import com.example.cns.common.FileEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Table(name = "message_file")
@Entity
@Getter
public class ChatFile extends FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
