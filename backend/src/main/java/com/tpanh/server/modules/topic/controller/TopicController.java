package com.tpanh.server.modules.topic.controller;

import com.tpanh.server.common.domain.PageResult;
import com.tpanh.server.common.dto.PageResponseDto;
import com.tpanh.server.modules.auth.security.CustomUserDetails;
import com.tpanh.server.modules.auth.service.UserService;
import com.tpanh.server.modules.topic.domain.Topic;
import com.tpanh.server.modules.topic.dto.CreateTopicRequestDto;
import com.tpanh.server.modules.topic.dto.TopicResponseDto;
import com.tpanh.server.modules.topic.dto.TopicSummaryResponseDto;
import com.tpanh.server.modules.topic.dto.UpdateTopicRequestDto;
import com.tpanh.server.modules.topic.enums.TopicStatus;
import com.tpanh.server.modules.topic.mapper.TopicMapper;
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
    private final TopicMapper topicMapper;

    @GetMapping
    public ResponseEntity<PageResponseDto<TopicSummaryResponseDto>> getPublishedTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(toSummaryPage(topicService.getPublishedTopics(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseDto> getTopicById(@PathVariable UUID id) {
        var topic = topicService.getTopicById(id);
        var creatorName = userService.getFullNameById(topic.getCreatorId());
        return ResponseEntity.ok(topicMapper.toResponse(topic, creatorName));
    }

    @PostMapping
    public ResponseEntity<TopicResponseDto> createTopic(
            @RequestBody @Valid CreateTopicRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var userId = userDetails.getUser().getId();
        var topic = topicMapper.toDomain(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(topicMapper.toResponse(topicService.createTopic(topic), userDetails.getUser().getFullName()));
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponseDto<TopicSummaryResponseDto>> getMyTopics(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var userId = userDetails.getUser().getId();
        var creatorName = userDetails.getUser().getFullName();
        var result = topicService.getTopicsByCreator(userId, page, size);

        var dtoPage = result.map(topic -> topicMapper.toSummaryResponse(topic, creatorName));

        return ResponseEntity.ok(PageResponseDto.fromDomain(dtoPage));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicResponseDto> updateTopic(
            @PathVariable UUID id,
            @RequestBody UpdateTopicRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var userId = userDetails.getUser().getId();
        var updateTopic = topicMapper.toUpdateDomain(request);
        var updated = topicService.updateTopic(id, userId, updateTopic);
        var creatorName = userDetails.getUser().getFullName();
        return ResponseEntity.ok(topicMapper.toResponse(updated, creatorName));
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
    public ResponseEntity<PageResponseDto<TopicSummaryResponseDto>> getTopicsByStatus(
            @PathVariable TopicStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var result = topicService.getTopicsByStatus(status, page, size);
        return ResponseEntity.ok(toSummaryPage(result));
    }

    private PageResponseDto<TopicSummaryResponseDto> toSummaryPage(PageResult<Topic> result) {
        var creatorIds = result.content().stream()
                .map(Topic::getCreatorId)
                .distinct()
                .toList();
        var creatorNames = userService.getFullNamesByIds(creatorIds);

        var dtoPage = result.map(topic -> topicMapper.toSummaryResponse(topic,
                creatorNames.getOrDefault(topic.getCreatorId(), "Unknown")));

        return PageResponseDto.fromDomain(dtoPage);
    }
}
