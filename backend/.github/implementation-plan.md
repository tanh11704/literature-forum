# Literature Forum - Implementation Plan & Database Analysis

## 1. Database Schema Analysis
Based on the provided database schema, the system consists of the following core entities and relationships:

### Implemented Entities
- **Auth & User Management**: `users`, `roles`, `user_roles`, `auth_providers`, `refresh_tokens`. (Module: `auth`)
- **Topic Management**: `topics`. (Module: `topic`)

### Pending Entities
- **Submissions**: `submissions` table.
  - Relationships: Belongs to a `topic` (topic_id), created by a `user` (author_id).
  - Constraints: UNIQUE(topic_id, author_id) - A user can only submit once per topic.
  - Statuses: DRAFT, SUBMITTED, APPROVED, REJECTED.
- **Ratings**: `ratings` table.
  - Relationships: Belongs to a `submission` (submission_id), created by a `user` (user_id).
  - Constraints: UNIQUE(submission_id, user_id) - A user can only rate a submission once.
- **Submission Reports**: `submission_reports` table.
  - Relationships: Belongs to a `submission` (submission_id), created by a `user` (reporter_id).
- **Comments**: `comments` table.
  - Relationships: Belongs to a `submission` (submission_id), created by a `user` (author_id), self-referencing for replies (parent_id).

## 2. Implementation Strategy
To maintain the Modular Monolith architecture and avoid overwhelming changes, the implementation must be broken down into small, isolated modules. **Do not attempt to implement multiple modules in a single PR or task.**

### Rule: One Module at a Time
When instructed to implement a feature, focus ONLY on the requested module. Follow the DDD layered architecture defined in `copilot-instructions.md`.

## 3. Module Breakdown & Task Plan

### Phase 1: Submission Module (`src/main/java/com/tpanh/server/modules/submission`)
**Goal**: Allow users to submit their work to a topic.
- **Task 1.1**: Create Flyway migration `V5__Create_Submissions_Table.sql`.
- **Task 1.2**: Implement `SubmissionEntity` (with manual FKs: `topicId`, `authorId`) and `SubmissionStatus` enum.
- **Task 1.3**: Implement Domain Model `Submission` and `SubmissionMapper` using MapStruct.
- **Task 1.4**: Implement `SubmissionRepository` (Interface + JpaRepository + Impl).
- **Task 1.5**: Implement `SubmissionService` (Create, Update, Delete, Change Status).
- **Task 1.6**: Implement `SubmissionController` and DTOs.

### Phase 2: Rating Module (`src/main/java/com/tpanh/server/modules/rating`)
**Goal**: Allow users to rate approved submissions.
- **Task 2.1**: Create Flyway migration `V6__Create_Ratings_Table.sql`.
- **Task 2.2**: Implement `RatingEntity` (manual FKs: `submissionId`, `userId`).
- **Task 2.3**: Implement Domain Model `Rating`.
- **Task 2.4**: Implement `RatingRepository`.
- **Task 2.5**: Implement `RatingService` (Add rating, update rating, calculate average).
- **Task 2.6**: Implement `RatingController` and DTOs.

### Phase 3: Report Module (`src/main/java/com/tpanh/server/modules/report`)
**Goal**: Allow users to report inappropriate submissions.
- **Task 3.1**: Create Flyway migration `V7__Create_Submission_Reports_Table.sql`.
- **Task 3.2**: Implement `SubmissionReportEntity` (manual FKs: `submissionId`, `reporterId`).
- **Task 3.3**: Implement Domain Model `SubmissionReport`.
- **Task 3.4**: Implement `ReportRepository`.
- **Task 3.5**: Implement `ReportService`.
- **Task 3.6**: Implement `ReportController` and DTOs.

### Phase 4: Comment Module (`src/main/java/com/tpanh/server/modules/comment`)
**Goal**: Allow users to comment on submissions and reply to comments.
- **Task 4.1**: Create Flyway migration `V8__Create_Comments_Table.sql`.
- **Task 4.2**: Implement `CommentEntity` (manual FKs: `submissionId`, `authorId`, `parentId`).
- **Task 4.3**: Implement Domain Model `Comment`.
- **Task 4.4**: Implement `CommentRepository`.
- **Task 4.5**: Implement `CommentService` (Add comment, reply, soft delete).
- **Task 4.6**: Implement `CommentController` and DTOs.

## 4. Agent Instructions
- **Read this file**: Before starting any new feature, review this plan to understand the current phase.
- **Strict Boundaries**: Do not create entities or repositories for other modules while working on the current one.
- **Manual FKs**: Remember to use `UUID` for foreign keys instead of JPA relationship annotations (`@ManyToOne`, etc.) for all new modules.
- **Mapping**: Use MapStruct for all object mapping (Entity <-> Domain <-> DTO). Do not write manual `toEntity()` or `fromEntity()` methods inside the Domain or Entity classes.
