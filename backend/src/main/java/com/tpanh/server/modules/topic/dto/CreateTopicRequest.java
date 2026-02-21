package com.tpanh.server.modules.topic.dto;

import com.tpanh.server.modules.topic.domain.Topic;
import com.tpanh.server.modules.topic.enums.TopicStatus;

import java.util.UUID;

public record CreateTopicRequest(
        String title,

        String content
) {
    public static Topic toDomain(CreateTopicRequest request, UUID creatorId) {
        return Topic.builder()
                .creatorId(creatorId)
                .title(request.title)
                .content(request.content)
                .status(TopicStatus.DRAFT)
                .build();
    }
}
