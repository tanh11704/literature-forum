package com.tpanh.server.modules.topic.dto;

import java.time.Instant;
import java.util.UUID;

public record TopicSummaryResponseDto(
        UUID id,
        UUID creatorId,
        String creatorName,
        String title,
        String status,
        Instant createdAt
) {
}
