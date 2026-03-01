package com.tpanh.server.modules.submission.repository;

import com.tpanh.server.modules.submission.entity.SubmissionEntity;
import com.tpanh.server.modules.submission.enums.SubmissionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<SubmissionEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SubmissionEntity s WHERE s.id = :id")
    Optional<SubmissionEntity> findByIdForUpdate(@Param("id") UUID id);

    Page<SubmissionEntity> findByTopicIdAndStatus(UUID topicId, SubmissionStatus status, Pageable pageable);

    Page<SubmissionEntity> findByAuthorId(UUID authorId, Pageable pageable);

    Page<SubmissionEntity> findByTopicId(UUID topicId, Pageable pageable);

    Page<SubmissionEntity> findByStatus(SubmissionStatus status, Pageable pageable);

    @Query("SELECT s FROM SubmissionEntity s WHERE (:topicId IS NULL OR s.topicId = :topicId) AND (:status IS NULL OR s.status = :status)")
    Page<SubmissionEntity> searchSubmissions(@Param("topicId") UUID topicId, @Param("status") SubmissionStatus status, Pageable pageable);
}
