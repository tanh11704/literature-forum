package com.tpanh.server.modules.topic.dto;

public record CreateTopicRequestDto(
        String title,
        String content
) {
}
