package com.tpanh.server.modules.topic.mapper;

import com.tpanh.server.modules.topic.domain.Topic;
import com.tpanh.server.modules.topic.domain.UpdateTopic;
import com.tpanh.server.modules.topic.dto.CreateTopicRequestDto;
import com.tpanh.server.modules.topic.dto.TopicResponseDto;
import com.tpanh.server.modules.topic.dto.TopicSummaryResponseDto;
import com.tpanh.server.modules.topic.dto.UpdateTopicRequestDto;
import com.tpanh.server.modules.topic.entity.TopicEntity;
import com.tpanh.server.modules.topic.enums.TopicStatus;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = {TopicStatus.class})
public interface TopicMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TopicEntity toEntity(Topic topic);

    Topic fromEntity(TopicEntity entity);

    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", expression = "java(TopicStatus.DRAFT)")
    @Mapping(target = "creatorId", source = "creatorId")
    @Mapping(target = "title", source = "createTopicRequestDto.title")
    @Mapping(target = "content", source = "createTopicRequestDto.content")
    Topic toDomain(CreateTopicRequestDto createTopicRequestDto, UUID creatorId);


    @Mapping(target = "creatorName", source = "creatorName")
    @Mapping(target = "status", expression = "java(topic.getStatus().name())")
    TopicResponseDto toResponse(Topic topic, String creatorName);

    @Mapping(target = "creatorName", source = "creatorName")
    @Mapping(target = "status", expression = "java(topic.getStatus().name())")
    TopicSummaryResponseDto toSummaryResponse(Topic topic, String creatorName);


    UpdateTopic toUpdateDomain(UpdateTopicRequestDto dto);


    // ── Partial Update (skip null & blank) ───────────────────────────

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creatorId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateTopic source, @MappingTarget TopicEntity target);

    @Condition
    default boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
