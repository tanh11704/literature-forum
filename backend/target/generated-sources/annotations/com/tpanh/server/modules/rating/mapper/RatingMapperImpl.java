package com.tpanh.server.modules.rating.mapper;

import com.tpanh.server.modules.rating.domain.Rating;
import com.tpanh.server.modules.rating.dto.CreateRatingRequestDto;
import com.tpanh.server.modules.rating.dto.RatingResponseDto;
import com.tpanh.server.modules.rating.dto.UpdateRatingRequestDto;
import com.tpanh.server.modules.rating.entity.RatingEntity;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-01T08:04:03+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class RatingMapperImpl implements RatingMapper {

    @Override
    public RatingEntity toEntity(Rating rating) {
        if ( rating == null ) {
            return null;
        }

        RatingEntity.RatingEntityBuilder ratingEntity = RatingEntity.builder();

        ratingEntity.id( rating.getId() );
        ratingEntity.submissionId( rating.getSubmissionId() );
        ratingEntity.userId( rating.getUserId() );
        ratingEntity.score( rating.getScore() );
        ratingEntity.createdAt( rating.getCreatedAt() );
        ratingEntity.updatedAt( rating.getUpdatedAt() );

        return ratingEntity.build();
    }

    @Override
    public Rating fromEntity(RatingEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Rating.RatingBuilder rating = Rating.builder();

        rating.id( entity.getId() );
        rating.submissionId( entity.getSubmissionId() );
        rating.userId( entity.getUserId() );
        rating.score( entity.getScore() );
        rating.createdAt( entity.getCreatedAt() );
        rating.updatedAt( entity.getUpdatedAt() );

        return rating.build();
    }

    @Override
    public Rating toDomain(CreateRatingRequestDto request, UUID submissionId, UUID userId) {
        if ( request == null && submissionId == null && userId == null ) {
            return null;
        }

        Rating.RatingBuilder rating = Rating.builder();

        if ( request != null ) {
            rating.score( request.score() );
        }
        rating.submissionId( submissionId );
        rating.userId( userId );

        return rating.build();
    }

    @Override
    public Rating toDomain(UpdateRatingRequestDto request, UUID submissionId, UUID userId) {
        if ( request == null && submissionId == null && userId == null ) {
            return null;
        }

        Rating.RatingBuilder rating = Rating.builder();

        if ( request != null ) {
            rating.score( request.score() );
        }
        rating.submissionId( submissionId );
        rating.userId( userId );

        return rating.build();
    }

    @Override
    public RatingResponseDto toResponse(Rating rating) {
        if ( rating == null ) {
            return null;
        }

        UUID id = null;
        UUID submissionId = null;
        UUID userId = null;
        Integer score = null;
        Instant createdAt = null;
        Instant updatedAt = null;

        id = rating.getId();
        submissionId = rating.getSubmissionId();
        userId = rating.getUserId();
        score = rating.getScore();
        createdAt = rating.getCreatedAt();
        updatedAt = rating.getUpdatedAt();

        RatingResponseDto ratingResponseDto = new RatingResponseDto( id, submissionId, userId, score, createdAt, updatedAt );

        return ratingResponseDto;
    }
}
