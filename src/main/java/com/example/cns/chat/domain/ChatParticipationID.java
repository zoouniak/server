package com.example.cns.chat.domain;

import com.example.cns.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipationID implements Serializable {
    private Member member;
    private ChatRoom room;

}
