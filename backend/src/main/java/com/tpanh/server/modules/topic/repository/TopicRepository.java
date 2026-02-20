package com.tpanh.server.modules.topic.repository;

import com.tpanh.server.modules.topic.entity.TopicEntity;
import com.tpanh.server.modules.topic.enums.TopicStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<TopicEntity, UUID> {

    Page<TopicEntity> findByStatus(TopicStatus status, Pageable pageable);

    Page<TopicEntity> findByCreatorId(UUID creatorId, Pageable pageable);

    Page<TopicEntity> findByCreatorIdAndStatus(UUID creatorId, TopicStatus status, Pageable pageable);

    Page<TopicEntity> findByStatusIn(List<TopicStatus> statuses, Pageable pageable);
}
