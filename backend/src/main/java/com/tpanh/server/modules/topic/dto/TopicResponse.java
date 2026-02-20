package com.tpanh.server.modules.topic.dto;

import com.tpanh.server.modules.topic.domain.Topic;

import java.time.Instant;
import java.util.UUID;

public record TopicResponse(
        UUID id,
        UUID creatorId,
        String creatorName,
        String title,
        String content,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static TopicResponse fromDomain(Topic topic, String creatorName) {
        return new TopicResponse(
                topic.getId(),
                topic.getCreatorId(),
                creatorName,
                topic.getTitle(),
                topic.getContent(),
                topic.getStatus().name(),
                topic.getCreatedAt(),
                topic.getUpdatedAt()
        );
    }
}
