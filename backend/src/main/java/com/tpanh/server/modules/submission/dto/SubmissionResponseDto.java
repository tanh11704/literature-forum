package com.tpanh.server.modules.submission.dto;

import com.tpanh.server.modules.submission.domain.Submission;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SubmissionResponseDto(
        UUID id,
        UUID topicId,
        String topicTitle,
        UUID authorId,
        String authorName,
        String title,
        String content,
        String status,
        Instant createdAt,
        Instant updatedAt,
        BigDecimal avgScore,
        Integer ratingCount) {
    public static SubmissionResponseDto fromDomain(Submission submission, String authorName) {
        return new SubmissionResponseDto(
                submission.getId(),
                submission.getTopicId(),
                submission.getTitle(),
                submission.getAuthorId(),
                authorName,
                submission.getTitle(),
                submission.getContent(),
                submission.getStatus().name(),
                submission.getCreatedAt(),
                submission.getUpdatedAt(),
                submission.getAvgScore(),
                submission.getRatingCount());
    }
}
