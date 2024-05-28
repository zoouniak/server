package com.example.cns.task.dto.response;

import com.example.cns.task.type.TodoState;

public record TaskResponse(
        String content,
        TodoState state
) {
}
