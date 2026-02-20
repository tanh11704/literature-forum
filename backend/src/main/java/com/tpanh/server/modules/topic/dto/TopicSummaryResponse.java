package com.tpanh.server.modules.topic.dto;

import com.tpanh.server.modules.topic.domain.Topic;

import java.time.Instant;
import java.util.UUID;

public record TopicSummaryResponse(
        UUID id,
        UUID creatorId,
        String creatorName,
        String title,
        String status,
        Instant createdAt
) {
    public static TopicSummaryResponse fromDomain(Topic topic, String creatorName) {
        return new TopicSummaryResponse(
                topic.getId(),
                topic.getCreatorId(),
                creatorName,
                topic.getTitle(),
                topic.getStatus().name(),
                topic.getCreatedAt()
        );
    }
}
