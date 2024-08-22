package com.example.cns.channel.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class ChannelParticipationID implements Serializable {
    private Long member;
    private Long channel;
}
