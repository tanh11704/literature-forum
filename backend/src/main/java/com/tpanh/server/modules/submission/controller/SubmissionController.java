package com.tpanh.server.modules.submission.controller;

import com.tpanh.server.common.domain.PageResult;
import com.tpanh.server.common.dto.PageResponseDto;
import com.tpanh.server.modules.auth.security.CustomUserDetails;
import com.tpanh.server.modules.auth.service.UserService;
import com.tpanh.server.modules.submission.domain.Submission;
import com.tpanh.server.modules.submission.dto.CreateSubmissionRequestDto;
import com.tpanh.server.modules.submission.dto.SubmissionResponseDto;
import com.tpanh.server.modules.submission.dto.SubmissionSummaryResponseDto;
import com.tpanh.server.modules.submission.dto.UpdateSubmissionRequestDto;
import com.tpanh.server.modules.submission.enums.SubmissionStatus;
import com.tpanh.server.modules.submission.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${application.api.prefix}/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<PageResponseDto<SubmissionSummaryResponseDto>> getApprovedSubmissionsByTopic(
            @RequestParam UUID topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(toSummaryPage(submissionService.getApprovedSubmissionsByTopic(topicId, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponseDto> getSubmissionById(@PathVariable UUID id) {
        var submission = submissionService.getSubmissionById(id);
        var authorName = userService.getFullNameById(submission.getAuthorId());
        return ResponseEntity.ok(SubmissionResponseDto.fromDomain(submission, authorName));
    }

    @PostMapping()
    public ResponseEntity<SubmissionResponseDto> createSubmission(
            @RequestBody @Valid CreateSubmissionRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var authorId = userDetails.getUser().getId();
        var submission = CreateSubmissionRequestDto.toDomain(request, authorId);
        var created = submissionService.createSubmission(submission);

        var authorName = userDetails.getUser().getFullName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SubmissionResponseDto.fromDomain(created, authorName));
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponseDto<SubmissionSummaryResponseDto>> getMySubmissions(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var authorId = userDetails.getUser().getId();
        var result = submissionService.getSubmissionsByAuthor(authorId, page, size);

        var authorName = userDetails.getUser().getFullName();
        var dtoPage = result.map(sub -> SubmissionSummaryResponseDto.fromDomain(sub, authorName));
        return ResponseEntity.ok(PageResponseDto.fromDomain(dtoPage));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubmissionResponseDto> updateSubmission(
            @PathVariable UUID id,
            @RequestBody UpdateSubmissionRequestDto updateSubmissionRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var updated = submissionService.updateSubmission(id,
                UpdateSubmissionRequestDto.toDomain(updateSubmissionRequestDto));

        var authorName = userDetails.getUser().getFullName();
        return ResponseEntity.ok(SubmissionResponseDto.fromDomain(updated, authorName));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(
            @PathVariable UUID id) {
        submissionService.deleteSubmission(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Void> submitForApproval(
            @PathVariable UUID id) {
        submissionService.submitForApproval(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveSubmission(@PathVariable UUID id) {
        submissionService.approveSubmission(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectSubmission(@PathVariable UUID id) {
        submissionService.rejectSubmission(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponseDto<SubmissionSummaryResponseDto>> searchSubmissions(
            @RequestParam(required = false) UUID topicId,
            @RequestParam(required = false) SubmissionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var result = submissionService.searchSubmissions(topicId, status, page, size);
        return ResponseEntity.ok(toSummaryPage(result));
    }

    private PageResponseDto<SubmissionSummaryResponseDto> toSummaryPage(PageResult<Submission> result) {
        var authorIds = result.content().stream()
                .map(Submission::getAuthorId)
                .distinct()
                .toList();
        var authorNames = userService.getFullNamesByIds(authorIds);

        var dtoPage = result.map(sub -> SubmissionSummaryResponseDto.fromDomain(sub,
                authorNames.getOrDefault(sub.getAuthorId(), "Unknown")));

        return PageResponseDto.fromDomain(dtoPage);
    }
}
