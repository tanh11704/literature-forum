package com.tpanh.server.modules.rating.service.impl;

import com.tpanh.server.common.exception.BusinessLogicException;
import com.tpanh.server.common.exception.ResourceNotFoundException;
import com.tpanh.server.modules.rating.domain.Rating;
import com.tpanh.server.modules.rating.entity.RatingEntity;
import com.tpanh.server.modules.rating.mapper.RatingMapper;
import com.tpanh.server.modules.rating.repository.RatingRepository;
import com.tpanh.server.modules.submission.entity.SubmissionEntity;
import com.tpanh.server.modules.submission.enums.SubmissionStatus;
import com.tpanh.server.modules.submission.repository.SubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RatingMapper ratingMapper;

    @Mock
    private SubmissionRepository submissionRepository;

    @InjectMocks
    private RatingServiceImpl ratingService;

    private UUID submissionId;
    private UUID authorId;
    private UUID raterId;
    private SubmissionEntity approvedSubmission;
    private Rating rating;
    private RatingEntity ratingEntity;

    @BeforeEach
    void setUp() {
        submissionId = UUID.randomUUID();
        authorId = UUID.randomUUID();
        raterId = UUID.randomUUID();

        approvedSubmission = SubmissionEntity.builder()
                .id(submissionId)
                .authorId(authorId)
                .status(SubmissionStatus.APPROVED)
                .avgScore(new BigDecimal("4.50"))
                .ratingCount(2)
                .build();

        rating = Rating.builder()
                .submissionId(submissionId)
                .userId(raterId)
                .score(5)
                .build();

        ratingEntity = RatingEntity.builder()
                .id(UUID.randomUUID())
                .submissionId(submissionId)
                .userId(raterId)
                .score(5)
                .createdAt(Instant.now())
                .build();

        lenient().when(ratingMapper.toEntity(any(Rating.class))).thenAnswer(invocation -> {
            var source = invocation.getArgument(0, Rating.class);
            return RatingEntity.builder()
                    .id(source.getId())
                    .submissionId(source.getSubmissionId())
                    .userId(source.getUserId())
                    .score(source.getScore())
                    .createdAt(source.getCreatedAt())
                    .updatedAt(source.getUpdatedAt())
                    .build();
        });
        lenient().when(ratingMapper.fromEntity(any(RatingEntity.class))).thenAnswer(invocation -> {
            var entity = invocation.getArgument(0, RatingEntity.class);
            return Rating.builder()
                    .id(entity.getId())
                    .submissionId(entity.getSubmissionId())
                    .userId(entity.getUserId())
                    .score(entity.getScore())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        });

                lenient().when(submissionRepository.findByIdForUpdate(submissionId)).thenReturn(Optional.of(approvedSubmission));
    }

    @Nested
    @DisplayName("createRating")
    class CreateRatingTests {

        @Test
        @DisplayName("Should create rating and update submission aggregates")
        void shouldCreateRating() {
            when(ratingRepository.findBySubmissionIdAndUserId(submissionId, raterId)).thenReturn(Optional.empty());
            when(ratingRepository.save(any(RatingEntity.class))).thenReturn(ratingEntity);

            var result = ratingService.createRating(rating);

            assertThat(result).isNotNull();
            assertThat(result.getScore()).isEqualTo(5);
            verify(submissionRepository).save(argThat(submission -> submission.getId().equals(submissionId)
                    && submission.getAvgScore().compareTo(new BigDecimal("4.67")) == 0
                    && submission.getRatingCount() == 3));
        }

        @Test
        @DisplayName("Should throw when submission is not approved")
        void shouldThrowWhenSubmissionNotApproved() {
            var draftSubmission = SubmissionEntity.builder()
                    .id(submissionId)
                    .authorId(authorId)
                    .status(SubmissionStatus.DRAFT)
                    .build();
                        when(submissionRepository.findByIdForUpdate(submissionId)).thenReturn(Optional.of(draftSubmission));

            assertThatThrownBy(() -> ratingService.createRating(rating))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasMessageContaining("APPROVED");
        }

        @Test
        @DisplayName("Should throw when user rates own submission")
        void shouldThrowWhenSelfRating() {
            var selfRating = Rating.builder()
                    .submissionId(submissionId)
                    .userId(authorId)
                    .score(4)
                    .build();

            assertThatThrownBy(() -> ratingService.createRating(selfRating))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasMessageContaining("own submission");
        }

        @Test
        @DisplayName("Should throw when duplicate rating exists")
        void shouldThrowWhenDuplicateRating() {
            when(ratingRepository.findBySubmissionIdAndUserId(submissionId, raterId))
                    .thenReturn(Optional.of(ratingEntity));

            assertThatThrownBy(() -> ratingService.createRating(rating))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasMessageContaining("already rated");
        }
    }

    @Nested
    @DisplayName("updateRating")
    class UpdateRatingTests {

        @Test
        @DisplayName("Should update existing rating and refresh aggregates")
        void shouldUpdateRating() {
            var update = Rating.builder()
                    .submissionId(submissionId)
                    .userId(raterId)
                    .score(3)
                    .build();

            when(ratingRepository.findById(ratingEntity.getId())).thenReturn(Optional.of(ratingEntity));
            when(ratingRepository.save(any(RatingEntity.class))).thenAnswer(invocation -> {
                var saved = invocation.getArgument(0, RatingEntity.class);
                saved.setUpdatedAt(Instant.now());
                return saved;
            });

            var result = ratingService.updateRating(ratingEntity.getId(), update);

            assertThat(result.getScore()).isEqualTo(3);
            verify(submissionRepository).save(argThat(submission -> submission.getId().equals(submissionId)
                                        && submission.getAvgScore().compareTo(new BigDecimal("3.50")) == 0
                    && submission.getRatingCount() == 2));
        }

        @Test
        @DisplayName("Should throw when rating is not found")
        void shouldThrowWhenRatingNotFound() {
            var missingRatingId = UUID.randomUUID();
            when(ratingRepository.findById(missingRatingId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ratingService.updateRating(missingRatingId, rating))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Rating");
        }

        @Test
        @DisplayName("Should throw when trying to update another user's rating")
        void shouldThrowWhenUpdatingAnotherUsersRating() {
            var anotherUserRating = RatingEntity.builder()
                    .id(UUID.randomUUID())
                    .submissionId(submissionId)
                    .userId(UUID.randomUUID())
                    .score(4)
                    .build();

            when(ratingRepository.findById(anotherUserRating.getId())).thenReturn(Optional.of(anotherUserRating));

            assertThatThrownBy(() -> ratingService.updateRating(anotherUserRating.getId(), rating))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasMessageContaining("own rating");
        }
    }

}
