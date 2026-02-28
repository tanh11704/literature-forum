package com.tpanh.server.modules.topic.service;

import com.tpanh.server.common.domain.PageResult;
import com.tpanh.server.modules.topic.domain.Topic;
import com.tpanh.server.modules.topic.domain.UpdateTopic;
import com.tpanh.server.modules.topic.enums.TopicStatus;

import java.util.UUID;

public interface TopicService {

    Topic createTopic(Topic topic);

    Topic getTopicById(UUID id);

    Topic updateTopic(UUID topicId, UUID requesterId, UpdateTopic updateTopic);

    void deleteTopic(UUID topicId, UUID requesterId);

    void submitForApproval(UUID topicId, UUID requesterId);

    void approveTopic(UUID topicId);

    void rejectTopic(UUID topicId);

    void archiveTopic(UUID topicId);

    void disableTopic(UUID topicId);

    PageResult<Topic> getPublishedTopics(int page, int size);

    PageResult<Topic> getTopicsByCreator(UUID creatorId, int page, int size);

    PageResult<Topic> getTopicsByStatus(TopicStatus status, int page, int size);
}
