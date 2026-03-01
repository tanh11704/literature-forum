package com.tpanh.server.modules.rating.mapper;

import com.tpanh.server.modules.rating.domain.Rating;
import com.tpanh.server.modules.rating.dto.CreateRatingRequestDto;
import com.tpanh.server.modules.rating.dto.RatingResponseDto;
import com.tpanh.server.modules.rating.dto.UpdateRatingRequestDto;
import com.tpanh.server.modules.rating.entity.RatingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    RatingEntity toEntity(Rating rating);

    Rating fromEntity(RatingEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "submissionId", source = "submissionId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "score", source = "request.score")
    Rating toDomain(CreateRatingRequestDto request, UUID submissionId, UUID userId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "submissionId", source = "submissionId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "score", source = "request.score")
    Rating toDomain(UpdateRatingRequestDto request, UUID submissionId, UUID userId);

    RatingResponseDto toResponse(Rating rating);

}
