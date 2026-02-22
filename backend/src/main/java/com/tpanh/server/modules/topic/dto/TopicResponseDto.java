package com.tpanh.server.modules.topic.dto;

import java.time.Instant;
import java.util.UUID;

public record TopicResponseDto(
        UUID id,
        UUID creatorId,
        String creatorName,
        String title,
        String content,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
