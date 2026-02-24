package com.tpanh.server.modules.topic.service.impl;

import com.tpanh.server.common.domain.PageResult;
import com.tpanh.server.common.exception.BusinessLogicException;
import com.tpanh.server.common.exception.ResourceNotFoundException;
import com.tpanh.server.modules.topic.domain.Topic;
import com.tpanh.server.modules.topic.domain.UpdateTopic;
import com.tpanh.server.modules.topic.enums.TopicStatus;
import com.tpanh.server.modules.topic.mapper.TopicMapper;
import com.tpanh.server.modules.topic.repository.TopicRepository;
import com.tpanh.server.modules.topic.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;

    @Override
    @Transactional
    public Topic createTopic(Topic topic) {
        return topicMapper.fromEntity(topicRepository.save(topicMapper.toEntity(topic)));
    }

    @Override
    @Transactional(readOnly = true)
    public Topic getTopicById(UUID id) {
        var entity = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", id));
        return topicMapper.fromEntity(entity);
    }

    @Override
    @Transactional
    public Topic updateTopic(UUID topicId, UUID requesterId, UpdateTopic updateTopic) {
        var entity = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));
        var topic = topicMapper.fromEntity(entity);

        if (!topic.isOwner(requesterId)) {
            throw new BusinessLogicException("You are not the owner of this topic");
        }
        requireEditableTopic(topic);

        topicMapper.updateEntity(updateTopic, entity);

        return topicMapper.fromEntity(entity);
    }

    @Override
    @Transactional
    public void deleteTopic(UUID topicId, UUID requesterId) {
        var topic = requireOwnedTopic(topicId, requesterId);
        if (!topic.isDeletable()) {
            throw new BusinessLogicException("Topic can only be deleted when in DRAFT status");
        }

        topicRepository.deleteById(topicId);
    }

    @Override
    @Transactional
    public void submitForApproval(UUID topicId, UUID requesterId) {
        var topic = requireOwnedTopic(topicId, requesterId);

        topic.submitForApproval();

        topicRepository.save(topicMapper.toEntity(topic));
    }

    @Override
    @Transactional
    public void approveTopic(UUID topicId) {
        var topic = getTopicById(topicId);
        topic.approve();
        topicRepository.save(topicMapper.toEntity(topic));
    }

    @Override
    @Transactional
    public void rejectTopic(UUID topicId) {
        var topic = getTopicById(topicId);
        topic.reject();
        topicRepository.save(topicMapper.toEntity(topic));
    }

    @Override
    @Transactional
    public void archiveTopic(UUID topicId) {
        var topic = getTopicById(topicId);
        topic.archive();
        topicRepository.save(topicMapper.toEntity(topic));
    }

    @Override
    @Transactional
    public void disableTopic(UUID topicId) {
        var topic = getTopicById(topicId);
        topic.disable();
        topicRepository.save(topicMapper.toEntity(topic));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Topic> getPublishedTopics(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var entityPage = topicRepository.findByStatus(TopicStatus.PUBLISHED, pageable);
        return PageResult.from(entityPage.map(topicMapper::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Topic> getTopicsByCreator(UUID creatorId, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var entityPage = topicRepository.findByCreatorId(creatorId, pageable);
        return PageResult.from(entityPage.map(topicMapper::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Topic> getTopicsByStatus(TopicStatus status, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var entityPage = topicRepository.findByStatus(status, pageable);
        return PageResult.from(entityPage.map(topicMapper::fromEntity));
    }

    private Topic requireOwnedTopic(UUID topicId, UUID requesterId) {
        var topic = getTopicById(topicId);
        if (!topic.isOwner(requesterId)) {
            throw new BusinessLogicException("You are not the owner of this topic");
        }
        return topic;
    }

    private void requireEditableTopic(Topic topic) {
        if (!topic.isEditable()) {
            throw new BusinessLogicException("Topic can only be edited when in DRAFT or REJECTED status");
        }
    }

}
