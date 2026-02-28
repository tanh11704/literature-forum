package com.tpanh.server.modules.submission.dto;

import com.tpanh.server.modules.submission.domain.Submission;

import java.time.Instant;
import java.util.UUID;

public record SubmissionSummaryResponseDto(
        UUID id,
        UUID authorId,
        String authorName,
        String title,
        String status,
        Instant createdAt
) {
    public static SubmissionSummaryResponseDto fromDomain(Submission submission, String authorName) {
        return new SubmissionSummaryResponseDto(
                submission.getId(),
                submission.getAuthorId(),
                authorName,
                submission.getTitle(),
                submission.getStatus().name(),
                submission.getCreatedAt()
        );
    }
}
