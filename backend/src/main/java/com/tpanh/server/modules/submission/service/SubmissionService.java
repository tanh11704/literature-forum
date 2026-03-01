package com.tpanh.server.modules.submission.service;

import com.tpanh.server.common.domain.PageResult;
import com.tpanh.server.modules.submission.domain.Submission;
import com.tpanh.server.modules.submission.domain.UpdateSubmissionRequest;
import com.tpanh.server.modules.submission.enums.SubmissionStatus;

import java.math.BigDecimal;
import java.util.UUID;

public interface SubmissionService {

    Submission createSubmission(Submission submission);

    Submission getSubmissionById(UUID id);

    Submission updateSubmission(UUID submissionId, UpdateSubmissionRequest updateSubmissionRequest);

    void deleteSubmission(UUID submissionId);

    void submitForApproval(UUID submissionId);

    void approveSubmission(UUID submissionId);

    void rejectSubmission(UUID submissionId);

    void updateRatingSummary(UUID submissionId, BigDecimal avgScore, int ratingCount);

    PageResult<Submission> getApprovedSubmissionsByTopic(UUID topicId, int page, int size);

    PageResult<Submission> getSubmissionsByAuthor(UUID authorId, int page, int size);

    PageResult<Submission> getSubmissionsByTopic(UUID topicId, int page, int size);

    PageResult<Submission> getSubmissionsByStatus(SubmissionStatus status, int page, int size);

    PageResult<Submission> searchSubmissions(UUID topicId, SubmissionStatus status, int page, int size);
}
