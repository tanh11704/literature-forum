package com.tpanh.server.modules.topic.domain;

import com.tpanh.server.common.exception.BusinessLogicException;
import com.tpanh.server.modules.topic.enums.TopicStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Topic {

    private UUID id;
    private UUID creatorId;
    private String title;
    private String content;
    private TopicStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public boolean isOwner(UUID userId) {
        return creatorId != null && creatorId.equals(userId);
    }

    public boolean isEditable() {
        return status == TopicStatus.DRAFT || status == TopicStatus.REJECTED;
    }

    public boolean isDeletable() {
        return status == TopicStatus.DRAFT;
    }

    public void submitForApproval() {
        if (!isEditable()) {
            throw new BusinessLogicException("Topic can only be submitted when in DRAFT or REJECTED status");
        }
        this.status = TopicStatus.PENDING_APPROVE;
    }

    public void approve() {
        if (status != TopicStatus.PENDING_APPROVE) {
            throw new BusinessLogicException("Only PENDING_APPROVE topics can be approved");
        }
        this.status = TopicStatus.PUBLISHED;
    }

    public void reject() {
        if (status != TopicStatus.PENDING_APPROVE) {
            throw new BusinessLogicException("Only PENDING_APPROVE topics can be rejected");
        }
        this.status = TopicStatus.REJECTED;
    }

    public void archive() {
        if (status != TopicStatus.PUBLISHED) {
            throw new BusinessLogicException("Only PUBLISHED topics can be archived");
        }
        this.status = TopicStatus.ARCHIVED;
    }

    public void disable() {
        if (status == TopicStatus.DISABLED) {
            throw new BusinessLogicException("Topic is already disabled");
        }
        this.status = TopicStatus.DISABLED;
    }
}
