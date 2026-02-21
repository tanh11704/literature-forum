package com.tpanh.server.modules.submission.dto;

import com.tpanh.server.modules.submission.domain.Submission;
import com.tpanh.server.modules.submission.enums.SubmissionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateSubmissionRequestDto(
        @NotNull(message = "Topic ID is required")
        UUID topicId,

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Content is required")
        String content
) {
    public static Submission toDomain(CreateSubmissionRequestDto createSubmissionRequest, UUID authorId) {
        return Submission.builder()
                .topicId(createSubmissionRequest.topicId)
                .authorId(authorId)
                .title(createSubmissionRequest.title)
                .content(createSubmissionRequest.content)
                .status(SubmissionStatus.DRAFT)
                .build();
    }
}
