package com.tpanh.server.modules.rating.domain;

import com.tpanh.server.common.exception.BusinessLogicException;
import com.tpanh.server.modules.submission.enums.SubmissionStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

    private UUID id;
    private UUID submissionId;
    private UUID userId;
    private Integer score;
    private Instant createdAt;
    private Instant updatedAt;

    public void validateCanRate(SubmissionStatus submissionStatus, UUID authorId) {
        if (submissionStatus != SubmissionStatus.APPROVED) {
            throw new BusinessLogicException("You can only rate APPROVED submissions");
        }

        if (authorId != null && authorId.equals(userId)) {
            throw new BusinessLogicException("You cannot rate your own submission");
        }
    }

    public void validateCanUpdate(UUID existingSubmissionId, UUID existingUserId) {
        if (!existingSubmissionId.equals(submissionId) || !existingUserId.equals(userId)) {
            throw new BusinessLogicException("You can only update your own rating for this submission");
        }
    }
}
