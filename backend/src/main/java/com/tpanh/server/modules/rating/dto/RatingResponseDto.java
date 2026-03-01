package com.tpanh.server.modules.rating.dto;

import java.time.Instant;
import java.util.UUID;

public record RatingResponseDto(
        UUID id,
        UUID submissionId,
        UUID userId,
        Integer score,
        Instant createdAt,
        Instant updatedAt
) {
}
