package com.example.cns.task.dto.response;

import com.example.cns.task.type.TodoState;

public record TaskResponse(
        Long id,
        String content,
        TodoState state
) {
}
