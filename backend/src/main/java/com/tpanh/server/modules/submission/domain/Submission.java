package com.tpanh.server.modules.submission.domain;

import com.tpanh.server.modules.submission.entity.SubmissionEntity;
import com.tpanh.server.modules.submission.enums.SubmissionStatus;
import lombok.*;

import java.math.BigDecimal;
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
    private BigDecimal avgScore;
    private Integer ratingCount;
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
                .avgScore(submission.getAvgScore())
                .ratingCount(submission.getRatingCount())
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
                .avgScore(submissionEntity.getAvgScore())
                .ratingCount(submissionEntity.getRatingCount())
                .createdAt(submissionEntity.getCreatedAt())
                .updatedAt(submissionEntity.getUpdatedAt())
                .build();
    }
}
