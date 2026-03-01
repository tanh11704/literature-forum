package com.tpanh.server.modules.submission.service.impl;

import com.tpanh.server.common.domain.PageResult;
import com.tpanh.server.common.exception.BusinessLogicException;
import com.tpanh.server.common.exception.ResourceNotFoundException;
import com.tpanh.server.modules.submission.domain.Submission;
import com.tpanh.server.modules.submission.domain.UpdateSubmissionRequest;
import com.tpanh.server.modules.submission.entity.SubmissionEntity;
import com.tpanh.server.modules.submission.enums.SubmissionStatus;
import com.tpanh.server.modules.submission.repository.SubmissionRepository;
import com.tpanh.server.modules.submission.service.SubmissionService;
import com.tpanh.server.modules.topic.enums.TopicStatus;
import com.tpanh.server.modules.topic.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final TopicService topicService;

    @Override
    @Transactional
    public Submission createSubmission(Submission submission) {
        var topic = topicService.getTopicById(submission.getTopicId());
        
        if (topic.getStatus() != TopicStatus.PUBLISHED) {
            throw new BusinessLogicException("Submissions can only be created for PUBLISHED topics");
        }

        return Submission.fromEntity(submissionRepository.save(Submission.toEntity(submission)));
    }

    @Override
    @Transactional(readOnly = true)
    public Submission getSubmissionById(UUID id) {
        var submissionEntity = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));
        return Submission.fromEntity(submissionEntity);
    }

    @Override
    @Transactional
    public Submission updateSubmission(UUID submissionId, UpdateSubmissionRequest updateSubmissionRequest) {

        var submissionEntity = getSubmissionEntityById(submissionId);

        submissionEntity.setTitle(updateSubmissionRequest.getTitle());
        submissionEntity.setContent(updateSubmissionRequest.getContent());
        
        return Submission.fromEntity(submissionRepository.save(submissionEntity));
    }

    @Override
    @Transactional
    public void deleteSubmission(UUID submissionId) {
        submissionRepository.deleteById(submissionId);
    }

    @Override
    @Transactional
    public void submitForApproval(UUID submissionId) {
        var submissionEntity = getSubmissionEntityById(submissionId);

        submissionEntity.setStatus(SubmissionStatus.SUBMITTED);

        submissionRepository.save(submissionEntity);
    }

    @Override
    @Transactional
    public void approveSubmission(UUID submissionId) {
        var submissionEntity = getSubmissionEntityById(submissionId);

        submissionEntity.setStatus(SubmissionStatus.APPROVED);

        submissionRepository.save(submissionEntity);
    }

    @Override
    @Transactional
    public void rejectSubmission(UUID submissionId) {
        var submissionEntity = getSubmissionEntityById(submissionId);

        submissionEntity.setStatus(SubmissionStatus.REJECTED);

        submissionRepository.save(submissionEntity);
    }

    @Override
    @Transactional
    public void updateRatingSummary(UUID submissionId, BigDecimal avgScore, int ratingCount) {
        var submissionEntity = getSubmissionEntityById(submissionId);

        submissionEntity.setAvgScore(avgScore);
        submissionEntity.setRatingCount(ratingCount);

        submissionRepository.save(submissionEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Submission> getApprovedSubmissionsByTopic(UUID topicId, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var entityPage = submissionRepository.findByTopicIdAndStatus(topicId, SubmissionStatus.APPROVED, pageable);
        return PageResult.from(entityPage.map(Submission::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Submission> getSubmissionsByAuthor(UUID authorId, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var entityPage = submissionRepository.findByAuthorId(authorId, pageable);
        return PageResult.from(entityPage.map(Submission::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Submission> getSubmissionsByTopic(UUID topicId, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var entityPage = submissionRepository.findByTopicId(topicId, pageable);
        return PageResult.from(entityPage.map(Submission::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Submission> getSubmissionsByStatus(SubmissionStatus status, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var entityPage = submissionRepository.findByStatus(status, pageable);
        return PageResult.from(entityPage.map(Submission::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Submission> searchSubmissions(UUID topicId, SubmissionStatus status, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var entityPage = submissionRepository.searchSubmissions(topicId, status, pageable);
        return PageResult.from(entityPage.map(Submission::fromEntity));
    }

    private SubmissionEntity getSubmissionEntityById(UUID id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));
    }

}
