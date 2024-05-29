package com.example.cns.task.dto.request;

import com.example.cns.task.type.TodoState;

public record TaskUpdateStateRequest(
        TodoState state
) {
}
