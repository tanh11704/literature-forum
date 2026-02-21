package com.tpanh.server.modules.topic.controller;

import com.tpanh.server.common.domain.PageResult;
import com.tpanh.server.common.dto.PageResponseDto;
import com.tpanh.server.modules.auth.security.CustomUserDetails;
import com.tpanh.server.modules.auth.service.UserService;
import com.tpanh.server.modules.topic.domain.Topic;
import com.tpanh.server.modules.topic.dto.CreateTopicRequest;
import com.tpanh.server.modules.topic.dto.TopicResponse;
import com.tpanh.server.modules.topic.dto.TopicSummaryResponse;
import com.tpanh.server.modules.topic.dto.UpdateTopicRequest;
import com.tpanh.server.modules.topic.enums.TopicStatus;
import com.tpanh.server.modules.topic.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${application.api.prefix}/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageResponseDto<TopicSummaryResponse>> getPublishedTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var result = topicService.getPublishedTopics(page, size);
        return ResponseEntity.ok(toSummaryPage(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponse> getTopicById(@PathVariable UUID id) {
        var topic = topicService.getTopicById(id);
        var creatorName = userService.getFullNameById(topic.getCreatorId());
        return ResponseEntity.ok(TopicResponse.fromDomain(topic, creatorName));
    }

    @PostMapping
    public ResponseEntity<TopicResponse> createTopic(
            @RequestBody @Valid CreateTopicRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var userId = userDetails.getUser().getId();
        var topic = CreateTopicRequest.toDomain(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TopicResponse.fromDomain(topicService.createTopic(topic), userDetails.getUser().getFullName()));
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponseDto<TopicSummaryResponse>> getMyTopics(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var userId = userDetails.getUser().getId();
        var creatorName = userDetails.getUser().getFullName();
        var result = topicService.getTopicsByCreator(userId, page, size);

        var dtoPage = result.map(topic -> TopicSummaryResponse.fromDomain(topic, creatorName));

        return ResponseEntity.ok(PageResponseDto.fromDomain(dtoPage));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicResponse> updateTopic(
            @PathVariable UUID id,
            @RequestBody UpdateTopicRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var userId = userDetails.getUser().getId();
        var updated = topicService.updateTopic(id, userId, request.title(), request.content());
        var creatorName = userDetails.getUser().getFullName();
        return ResponseEntity.ok(TopicResponse.fromDomain(updated, creatorName));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var userId = userDetails.getUser().getId();
        topicService.deleteTopic(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Void> submitForApproval(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var userId = userDetails.getUser().getId();
        topicService.submitForApproval(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveTopic(@PathVariable UUID id) {
        topicService.approveTopic(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectTopic(@PathVariable UUID id) {
        topicService.rejectTopic(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> archiveTopic(@PathVariable UUID id) {
        topicService.archiveTopic(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> disableTopic(@PathVariable UUID id) {
        topicService.disableTopic(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponseDto<TopicSummaryResponse>> getTopicsByStatus(
            @PathVariable TopicStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var result = topicService.getTopicsByStatus(status, page, size);
        return ResponseEntity.ok(toSummaryPage(result));
    }

    private PageResponseDto<TopicSummaryResponse> toSummaryPage(PageResult<Topic> result) {
        var creatorIds = result.content().stream()
                .map(Topic::getCreatorId)
                .distinct()
                .toList();
        var creatorNames = userService.getFullNamesByIds(creatorIds);

        var dtoPage = result.map(topic -> TopicSummaryResponse.fromDomain(topic,
                creatorNames.getOrDefault(topic.getCreatorId(), "Unknown")));

        return PageResponseDto.fromDomain(dtoPage);
    }
}
