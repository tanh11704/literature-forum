package com.tpanh.server.modules.topic.dto;

public record UpdateTopicRequestDto(
        String title,
        String content
) {
}
