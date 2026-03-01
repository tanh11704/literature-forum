package com.tpanh.server.modules.rating.repository;

import com.tpanh.server.modules.rating.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<RatingEntity, UUID> {

    Optional<RatingEntity> findBySubmissionIdAndUserId(UUID submissionId, UUID userId);

    long countBySubmissionId(UUID submissionId);

    @Query("SELECT AVG(r.score) FROM RatingEntity r WHERE r.submissionId = :submissionId")
    Double getAverageScoreBySubmissionId(@Param("submissionId") UUID submissionId);
}
