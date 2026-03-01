package com.tpanh.server.modules.rating.service;

import com.tpanh.server.modules.rating.domain.Rating;

import java.util.UUID;

public interface RatingService {

    Rating createRating(Rating rating);

    Rating updateRating(UUID ratingId, Rating rating);

    Rating getMyRating(UUID submissionId, UUID userId);

}
