package com.tpanh.server.modules.topic.dto;

public record UpdateTopicRequest(
        String title,
        String content
) {
}
