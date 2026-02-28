package com.tpanh.server.modules.submission.domain;

import com.tpanh.server.modules.submission.entity.SubmissionEntity;
import com.tpanh.server.modules.submission.enums.SubmissionStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    private UUID id;
    private UUID topicId;
    private UUID authorId;
    private String title;
    private String content;
    private SubmissionStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public static SubmissionEntity toEntity(Submission submission) {
        return SubmissionEntity.builder()
                .id(submission.getId())
                .topicId(submission.getTopicId())
                .authorId(submission.getAuthorId())
                .title(submission.getTitle())
                .content(submission.getContent())
                .status(submission.getStatus())
                .build();
    }

    public static Submission fromEntity(SubmissionEntity submissionEntity) {
        return Submission.builder()
                .id(submissionEntity.getId())
                .topicId(submissionEntity.getTopicId())
                .authorId(submissionEntity.getAuthorId())
                .title(submissionEntity.getTitle())
                .content(submissionEntity.getContent())
                .status(submissionEntity.getStatus())
                .createdAt(submissionEntity.getCreatedAt())
                .updatedAt(submissionEntity.getUpdatedAt())
                .build();
    }
}
