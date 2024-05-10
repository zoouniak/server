package com.example.cns.chat.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipationID implements Serializable {
    private Long member;
    private Long room;

}
