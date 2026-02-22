package com.tpanh.server.modules.topic.mapper;

import com.tpanh.server.modules.topic.domain.Topic;
import com.tpanh.server.modules.topic.domain.UpdateTopic;
import com.tpanh.server.modules.topic.dto.CreateTopicRequestDto;
import com.tpanh.server.modules.topic.dto.TopicResponseDto;
import com.tpanh.server.modules.topic.dto.TopicSummaryResponseDto;
import com.tpanh.server.modules.topic.dto.UpdateTopicRequestDto;
import com.tpanh.server.modules.topic.entity.TopicEntity;
import com.tpanh.server.modules.topic.enums.TopicStatus;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-22T17:09:04+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class TopicMapperImpl implements TopicMapper {

    @Override
    public TopicEntity toEntity(Topic topic) {
        if ( topic == null ) {
            return null;
        }

        TopicEntity.TopicEntityBuilder topicEntity = TopicEntity.builder();

        topicEntity.id( topic.getId() );
        topicEntity.creatorId( topic.getCreatorId() );
        if ( isNotBlank( topic.getTitle() ) ) {
            topicEntity.title( topic.getTitle() );
        }
        if ( isNotBlank( topic.getContent() ) ) {
            topicEntity.content( topic.getContent() );
        }
        topicEntity.status( topic.getStatus() );

        return topicEntity.build();
    }

    @Override
    public Topic fromEntity(TopicEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Topic.TopicBuilder topic = Topic.builder();

        topic.id( entity.getId() );
        topic.creatorId( entity.getCreatorId() );
        if ( isNotBlank( entity.getTitle() ) ) {
            topic.title( entity.getTitle() );
        }
        if ( isNotBlank( entity.getContent() ) ) {
            topic.content( entity.getContent() );
        }
        topic.status( entity.getStatus() );
        topic.createdAt( entity.getCreatedAt() );
        topic.updatedAt( entity.getUpdatedAt() );

        return topic.build();
    }

    @Override
    public Topic toDomain(CreateTopicRequestDto createTopicRequestDto, UUID creatorId) {
        if ( createTopicRequestDto == null && creatorId == null ) {
            return null;
        }

        Topic.TopicBuilder topic = Topic.builder();

        if ( createTopicRequestDto != null ) {
            if ( isNotBlank( createTopicRequestDto.title() ) ) {
                topic.title( createTopicRequestDto.title() );
            }
            if ( isNotBlank( createTopicRequestDto.content() ) ) {
                topic.content( createTopicRequestDto.content() );
            }
        }
        topic.creatorId( creatorId );
        topic.status( TopicStatus.DRAFT );

        return topic.build();
    }

    @Override
    public TopicResponseDto toResponse(Topic topic, String creatorName) {
        if ( topic == null && creatorName == null ) {
            return null;
        }

        UUID id = null;
        UUID creatorId = null;
        String title = null;
        String content = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        if ( topic != null ) {
            if ( isNotBlank( creatorName ) ) {
                id = topic.getId();
            }
            if ( isNotBlank( creatorName ) ) {
                creatorId = topic.getCreatorId();
            }
            if ( isNotBlank( topic.getTitle() ) ) {
                title = topic.getTitle();
            }
            if ( isNotBlank( topic.getContent() ) ) {
                content = topic.getContent();
            }
            if ( isNotBlank( creatorName ) ) {
                createdAt = topic.getCreatedAt();
            }
            if ( isNotBlank( creatorName ) ) {
                updatedAt = topic.getUpdatedAt();
            }
        }
        String creatorName1 = null;
        if ( isNotBlank( creatorName ) ) {
            creatorName1 = creatorName;
        }

        String status = topic.getStatus().name();

        TopicResponseDto topicResponseDto = new TopicResponseDto( id, creatorId, creatorName1, title, content, status, createdAt, updatedAt );

        return topicResponseDto;
    }

    @Override
    public TopicSummaryResponseDto toSummaryResponse(Topic topic, String creatorName) {
        if ( topic == null && creatorName == null ) {
            return null;
        }

        UUID id = null;
        UUID creatorId = null;
        String title = null;
        Instant createdAt = null;
        if ( topic != null ) {
            if ( isNotBlank( creatorName ) ) {
                id = topic.getId();
            }
            if ( isNotBlank( creatorName ) ) {
                creatorId = topic.getCreatorId();
            }
            if ( isNotBlank( topic.getTitle() ) ) {
                title = topic.getTitle();
            }
            if ( isNotBlank( creatorName ) ) {
                createdAt = topic.getCreatedAt();
            }
        }
        String creatorName1 = null;
        if ( isNotBlank( creatorName ) ) {
            creatorName1 = creatorName;
        }

        String status = topic.getStatus().name();

        TopicSummaryResponseDto topicSummaryResponseDto = new TopicSummaryResponseDto( id, creatorId, creatorName1, title, status, createdAt );

        return topicSummaryResponseDto;
    }

    @Override
    public UpdateTopic toUpdateDomain(UpdateTopicRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        UpdateTopic.UpdateTopicBuilder updateTopic = UpdateTopic.builder();

        if ( isNotBlank( dto.title() ) ) {
            updateTopic.title( dto.title() );
        }
        if ( isNotBlank( dto.content() ) ) {
            updateTopic.content( dto.content() );
        }

        return updateTopic.build();
    }

    @Override
    public void updateEntity(UpdateTopic source, TopicEntity target) {
        if ( source == null ) {
            return;
        }

        if ( isNotBlank( source.getTitle() ) ) {
            target.setTitle( source.getTitle() );
        }
        if ( isNotBlank( source.getContent() ) ) {
            target.setContent( source.getContent() );
        }
    }
}
