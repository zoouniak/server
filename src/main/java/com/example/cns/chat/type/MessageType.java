package com.example.cns.chat.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MessageType {
    POST("POST"),
    IMAGE("IMAGE"),
    TEXT("TEXT"),
    FILE("FILE"),
    STATUS("STATUS");
    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
