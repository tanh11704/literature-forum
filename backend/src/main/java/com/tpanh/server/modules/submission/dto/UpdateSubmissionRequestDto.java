package com.tpanh.server.modules.submission.dto;

import com.tpanh.server.modules.submission.domain.UpdateSubmissionRequest;

public record UpdateSubmissionRequestDto(
                String title,
                String content) {

        public static UpdateSubmissionRequestDto fromDomain(UpdateSubmissionRequest updateSubmissionRequest) {
                return new UpdateSubmissionRequestDto(
                                updateSubmissionRequest.getTitle(),
                                updateSubmissionRequest.getContent());
        }

        public static UpdateSubmissionRequest toDomain(UpdateSubmissionRequestDto updateSubmissionRequestDto) {
                return new UpdateSubmissionRequest(
                                updateSubmissionRequestDto.title(),
                                updateSubmissionRequestDto.content());
        }
}
