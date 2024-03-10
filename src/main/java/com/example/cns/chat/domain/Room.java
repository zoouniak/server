package com.example.cns.chat.domain;

import com.example.cns.chat.type.RoomType;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private RoomType type;

    @Column
    private String name;
}
