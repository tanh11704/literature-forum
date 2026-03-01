package com.tpanh.server.modules.rating.service.impl;

import com.tpanh.server.common.exception.BusinessLogicException;
import com.tpanh.server.common.exception.ResourceNotFoundException;
import com.tpanh.server.modules.rating.domain.Rating;
import com.tpanh.server.modules.rating.mapper.RatingMapper;
import com.tpanh.server.modules.rating.repository.RatingRepository;
import com.tpanh.server.modules.rating.service.RatingService;
import com.tpanh.server.modules.submission.entity.SubmissionEntity;
import com.tpanh.server.modules.submission.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private static final int SCORE_SCALE = 2;

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final SubmissionRepository submissionRepository;

    @Override
    @Transactional
    public Rating createRating(Rating rating) {
        var submission = requireSubmission(rating.getSubmissionId());
        rating.validateCanRate(submission.getStatus(), submission.getAuthorId());

        ratingRepository.findBySubmissionIdAndUserId(rating.getSubmissionId(), rating.getUserId())
                .ifPresent(existing -> {
                    throw new BusinessLogicException("You have already rated this submission");
                });

        var saved = ratingRepository.save(ratingMapper.toEntity(rating));
        applyCreateRatingSummary(submission, rating.getScore());

        return ratingMapper.fromEntity(saved);
    }

    @Override
    @Transactional
    public Rating updateRating(UUID ratingId, Rating rating) {
        var submission = requireSubmission(rating.getSubmissionId());
        rating.validateCanRate(submission.getStatus(), submission.getAuthorId());

        var existing = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating", "id", ratingId));
        rating.validateCanUpdate(existing.getSubmissionId(), existing.getUserId());

        var toUpdate = ratingMapper.fromEntity(existing);
        toUpdate.setScore(rating.getScore());

        var saved = ratingRepository.save(ratingMapper.toEntity(toUpdate));
        applyUpdateRatingSummary(submission, existing.getScore(), rating.getScore());

        return ratingMapper.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Rating getMyRating(UUID submissionId, UUID userId) {
        var entity = ratingRepository.findBySubmissionIdAndUserId(submissionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating", "submissionId and userId",
                        submissionId + " / " + userId));
                        
        return ratingMapper.fromEntity(entity);
    }

    private void applyCreateRatingSummary(SubmissionEntity submission, int newScore) {
        var currentCount = submission.getRatingCount() == null ? 0 : submission.getRatingCount();
        var currentAvg = normalizeScore(submission.getAvgScore());

        var nextCount = currentCount + 1;
        var nextAvg = currentAvg
                .multiply(BigDecimal.valueOf(currentCount))
                .add(BigDecimal.valueOf(newScore))
                .divide(BigDecimal.valueOf(nextCount), SCORE_SCALE, RoundingMode.HALF_UP);

        submission.setAvgScore(nextAvg);
        submission.setRatingCount(nextCount);

        submissionRepository.save(submission);
    }

    private void applyUpdateRatingSummary(SubmissionEntity submission, int oldScore, int newScore) {
        var currentCount = submission.getRatingCount() == null ? 0 : submission.getRatingCount();

        if (currentCount <= 0) {
            throw new BusinessLogicException("Invalid rating count when updating rating summary");
        }

        var currentAvg = normalizeScore(submission.getAvgScore());
        var nextAvg = currentAvg
                .multiply(BigDecimal.valueOf(currentCount))
                .subtract(BigDecimal.valueOf(oldScore))
                .add(BigDecimal.valueOf(newScore))
                .divide(BigDecimal.valueOf(currentCount), SCORE_SCALE, RoundingMode.HALF_UP);

        submission.setAvgScore(nextAvg);
        submissionRepository.save(submission);
    }

    private BigDecimal normalizeScore(BigDecimal score) {
        return (score == null ? BigDecimal.ZERO : score).setScale(SCORE_SCALE, RoundingMode.HALF_UP);
    }

    private SubmissionEntity requireSubmission(UUID submissionId) {
        return submissionRepository.findByIdForUpdate(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", submissionId));
    }
}
