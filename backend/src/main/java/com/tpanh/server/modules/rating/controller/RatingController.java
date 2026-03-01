package com.tpanh.server.modules.rating.controller;

import com.tpanh.server.modules.auth.security.CustomUserDetails;
import com.tpanh.server.modules.rating.dto.CreateRatingRequestDto;
import com.tpanh.server.modules.rating.dto.RatingResponseDto;
import com.tpanh.server.modules.rating.dto.UpdateRatingRequestDto;
import com.tpanh.server.modules.rating.mapper.RatingMapper;
import com.tpanh.server.modules.rating.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${application.api.prefix}/submissions/{submissionId}/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final RatingMapper ratingMapper;

    @PostMapping
    public ResponseEntity<RatingResponseDto> createRating(
            @PathVariable UUID submissionId,
            @RequestBody @Valid CreateRatingRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var created = ratingService.createRating(
                ratingMapper.toDomain(request,
                        submissionId,
                        userDetails.getUser().getId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(ratingMapper.toResponse(created));
    }

    @PutMapping("/{ratingId}")
    public ResponseEntity<RatingResponseDto> updateRating(
            @PathVariable UUID submissionId,
            @PathVariable UUID ratingId,
            @RequestBody @Valid UpdateRatingRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var updated = ratingService.updateRating(
                ratingId,
                ratingMapper.toDomain(request,
                        submissionId,
                        userDetails.getUser().getId()));

        return ResponseEntity.ok(ratingMapper.toResponse(updated));
    }

    @GetMapping("/my")
    public ResponseEntity<RatingResponseDto> getMyRating(
            @PathVariable UUID submissionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var rating = ratingService.getMyRating(
                submissionId,
                userDetails.getUser().getId());

        return ResponseEntity.ok(ratingMapper.toResponse(rating));
    }

}
