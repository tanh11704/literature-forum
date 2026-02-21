package com.tpanh.server.modules.topic.service.impl;

import com.tpanh.server.common.domain.PageResult;
import com.tpanh.server.common.exception.BusinessLogicException;
import com.tpanh.server.common.exception.ResourceNotFoundException;
import com.tpanh.server.modules.topic.domain.Topic;
import com.tpanh.server.modules.topic.entity.TopicEntity;
import com.tpanh.server.modules.topic.enums.TopicStatus;
import com.tpanh.server.modules.topic.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceImplTest {

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private TopicServiceImpl topicService;

    private UUID topicId;
    private UUID creatorId;
    private UUID otherUserId;
    private TopicEntity draftEntity;
    private TopicEntity publishedEntity;
    private TopicEntity pendingEntity;

    @BeforeEach
    void setUp() {
        topicId = UUID.randomUUID();
        creatorId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();

        draftEntity = TopicEntity.builder()
                .id(topicId)
                .creatorId(creatorId)
                .title("Test Topic")
                .content("Test Content")
                .status(TopicStatus.DRAFT)
                .createdAt(Instant.now())
                .build();

        publishedEntity = TopicEntity.builder()
                .id(topicId)
                .creatorId(creatorId)
                .title("Published Topic")
                .content("Published Content")
                .status(TopicStatus.PUBLISHED)
                .createdAt(Instant.now())
                .build();

        pendingEntity = TopicEntity.builder()
                .id(topicId)
                .creatorId(creatorId)
                .title("Pending Topic")
                .content("Pending Content")
                .status(TopicStatus.PENDING_APPROVE)
                .createdAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("createTopic")
    class CreateTopicTests {

        @Test
        @DisplayName("Should create and return topic successfully")
        void shouldCreateTopic() {
            var topic = Topic.builder()
                    .creatorId(creatorId)
                    .title("New Topic")
                    .content("New Content")
                    .status(TopicStatus.DRAFT)
                    .build();

            var savedEntity = TopicEntity.builder()
                    .id(topicId)
                    .creatorId(creatorId)
                    .title("New Topic")
                    .content("New Content")
                    .status(TopicStatus.DRAFT)
                    .createdAt(Instant.now())
                    .build();

            when(topicRepository.save(any(TopicEntity.class))).thenReturn(savedEntity);

            var result = topicService.createTopic(topic);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(topicId);
            assertThat(result.getTitle()).isEqualTo("New Topic");
            assertThat(result.getStatus()).isEqualTo(TopicStatus.DRAFT);
            verify(topicRepository).save(any(TopicEntity.class));
        }
    }

    @Nested
    @DisplayName("getTopicById")
    class GetTopicByIdTests {

        @Test
        @DisplayName("Should return topic when found")
        void shouldReturnTopicWhenFound() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(draftEntity));

            var result = topicService.getTopicById(topicId);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(topicId);
            assertThat(result.getTitle()).isEqualTo("Test Topic");
            verify(topicRepository).findById(topicId);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> topicService.getTopicById(topicId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Topic");

            verify(topicRepository).findById(topicId);
        }
    }

    @Nested
    @DisplayName("updateTopic")
    class UpdateTopicTests {

        @Test
        @DisplayName("Should update title and content when both provided")
        void shouldUpdateTitleAndContent() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(draftEntity));
            when(topicRepository.save(any(TopicEntity.class))).thenReturn(draftEntity);

            var result = topicService.updateTopic(topicId, creatorId, "Updated Title", "Updated Content");

            assertThat(result).isNotNull();
            verify(topicRepository).save(any(TopicEntity.class));
        }

        @Test
        @DisplayName("Should update only title when content is null")
        void shouldUpdateOnlyTitle() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(draftEntity));
            when(topicRepository.save(any(TopicEntity.class))).thenReturn(draftEntity);

            topicService.updateTopic(topicId, creatorId, "Updated Title", null);

            verify(topicRepository).save(any(TopicEntity.class));
        }

        @Test
        @DisplayName("Should update only content when title is null")
        void shouldUpdateOnlyContent() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(draftEntity));
            when(topicRepository.save(any(TopicEntity.class))).thenReturn(draftEntity);

            topicService.updateTopic(topicId, creatorId, null, "Updated Content");

            verify(topicRepository).save(any(TopicEntity.class));
        }

        @Test
        @DisplayName("Should skip blank title")
        void shouldSkipBlankTitle() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(draftEntity));
            when(topicRepository.save(any(TopicEntity.class))).thenReturn(draftEntity);

            topicService.updateTopic(topicId, creatorId, "   ", "Updated Content");

            verify(topicRepository).save(any(TopicEntity.class));
        }

        @Test
        @DisplayName("Should throw when requester is not the owner")
        void shouldThrowWhenNotOwner() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(draftEntity));

            assertThatThrownBy(() -> topicService.updateTopic(topicId, otherUserId, "Title", "Content"))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasMessageContaining("not the owner");
        }

        @Test
        @DisplayName("Should throw when topic is not editable (PUBLISHED)")
        void shouldThrowWhenNotEditable() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(publishedEntity));

            assertThatThrownBy(() -> topicService.updateTopic(topicId, creatorId, "Title", "Content"))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasMessageContaining("DRAFT or REJECTED");
        }

        @Test
        @DisplayName("Should allow editing REJECTED topic")
        void shouldAllowEditingRejectedTopic() {
            var rejectedEntity = TopicEntity.builder()
                    .id(topicId)
                    .creatorId(creatorId)
                    .title("Rejected Topic")
                    .content("Old Content")
                    .status(TopicStatus.REJECTED)
                    .createdAt(Instant.now())
                    .build();

            when(topicRepository.findById(topicId)).thenReturn(Optional.of(rejectedEntity));
            when(topicRepository.save(any(TopicEntity.class))).thenReturn(rejectedEntity);

            var result = topicService.updateTopic(topicId, creatorId, "Fixed Title", "Fixed Content");

            assertThat(result).isNotNull();
            verify(topicRepository).save(any(TopicEntity.class));
        }
    }

    @Nested
    @DisplayName("deleteTopic")
    class DeleteTopicTests {

        @Test
        @DisplayName("Should delete DRAFT topic owned by requester")
        void shouldDeleteDraftTopic() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(draftEntity));

            topicService.deleteTopic(topicId, creatorId);

            verify(topicRepository).deleteById(topicId);
        }

        @Test
        @DisplayName("Should throw when not owner")
        void shouldThrowWhenNotOwner() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(draftEntity));

            assertThatThrownBy(() -> topicService.deleteTopic(topicId, otherUserId))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasMessageContaining("not the owner");

            verify(topicRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should throw when topic is not DRAFT")
        void shouldThrowWhenNotDraft() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(publishedEntity));

            assertThatThrownBy(() -> topicService.deleteTopic(topicId, creatorId))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasMessageContaining("DRAFT");

            verify(topicRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("submitForApproval")
    class SubmitForApprovalTests {

        @Test
        @DisplayName("Should submit DRAFT topic for approval")
        void shouldSubmitDraftTopic() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(draftEntity));
            when(topicRepository.save(any(TopicEntity.class))).thenReturn(pendingEntity);

            topicService.submitForApproval(topicId, creatorId);

            verify(topicRepository).save(any(TopicEntity.class));
        }

        @Test
        @DisplayName("Should submit REJECTED topic for approval")
        void shouldSubmitRejectedTopic() {
            var rejectedEntity = TopicEntity.builder()
                    .id(topicId)
                    .creatorId(creatorId)
                    .title("Rejected Topic")
                    .content("Content")
                    .status(TopicStatus.REJECTED)
                    .createdAt(Instant.now())
                    .build();

            when(topicRepository.findById(topicId)).thenReturn(Optional.of(rejectedEntity));
            when(topicRepository.save(any(TopicEntity.class))).thenReturn(pendingEntity);

            topicService.submitForApproval(topicId, creatorId);

            verify(topicRepository).save(any(TopicEntity.class));
        }

        @Test
        @DisplayName("Should throw when topic is not owner")
        void shouldThrowWhenNotOwner() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(draftEntity));

            assertThatThrownBy(() -> topicService.submitForApproval(topicId, otherUserId))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasMessageContaining("not the owner");
        }

        @Test
        @DisplayName("Should throw when PUBLISHED topic submits")
        void shouldThrowWhenPublished() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(publishedEntity));

            assertThatThrownBy(() -> topicService.submitForApproval(topicId, creatorId))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasMessageContaining("DRAFT or REJECTED");
        }
    }

    @Nested
    @DisplayName("approveTopic")
    class ApproveTopicTests {

        @Test
        @DisplayName("Should approve PENDING_APPROVE topic")
        void shouldApprovePendingTopic() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(pendingEntity));
            when(topicRepository.save(any(TopicEntity.class))).thenReturn(publishedEntity);

            topicService.approveTopic(topicId);

            verify(topicRepository).save(any(TopicEntity.class));
        }

        @Test
        @DisplayName("Should throw when approving non-PENDING topic")
        void shouldThrowWhenNotPending() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(draftEntity));

            assertThatThrownBy(() -> topicService.approveTopic(topicId))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasMessageContaining("PENDING_APPROVE");
        }
    }

    @Nested
    @DisplayName("rejectTopic")
    class RejectTopicTests {

        @Test
        @DisplayName("Should reject PENDING_APPROVE topic")
        void shouldRejectPendingTopic() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(pendingEntity));
            when(topicRepository.save(any(TopicEntity.class))).thenReturn(pendingEntity);

            topicService.rejectTopic(topicId);

            verify(topicRepository).save(any(TopicEntity.class));
        }

        @Test
        @DisplayName("Should throw when rejecting non-PENDING topic")
        void shouldThrowWhenNotPending() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(draftEntity));

            assertThatThrownBy(() -> topicService.rejectTopic(topicId))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasMessageContaining("PENDING_APPROVE");
        }
    }

    @Nested
    @DisplayName("archiveTopic")
    class ArchiveTopicTests {

        @Test
        @DisplayName("Should archive PUBLISHED topic")
        void shouldArchivePublishedTopic() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(publishedEntity));
            when(topicRepository.save(any(TopicEntity.class))).thenReturn(publishedEntity);

            topicService.archiveTopic(topicId);

            verify(topicRepository).save(any(TopicEntity.class));
        }

        @Test
        @DisplayName("Should throw when archiving non-PUBLISHED topic")
        void shouldThrowWhenNotPublished() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(draftEntity));

            assertThatThrownBy(() -> topicService.archiveTopic(topicId))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasMessageContaining("PUBLISHED");
        }
    }

    @Nested
    @DisplayName("disableTopic")
    class DisableTopicTests {

        @Test
        @DisplayName("Should disable topic")
        void shouldDisableTopic() {
            when(topicRepository.findById(topicId)).thenReturn(Optional.of(draftEntity));
            when(topicRepository.save(any(TopicEntity.class))).thenReturn(draftEntity);

            topicService.disableTopic(topicId);

            verify(topicRepository).save(any(TopicEntity.class));
        }

        @Test
        @DisplayName("Should throw when already DISABLED")
        void shouldThrowWhenAlreadyDisabled() {
            var disabledEntity = TopicEntity.builder()
                    .id(topicId)
                    .creatorId(creatorId)
                    .title("Disabled Topic")
                    .content("Content")
                    .status(TopicStatus.DISABLED)
                    .createdAt(Instant.now())
                    .build();

            when(topicRepository.findById(topicId)).thenReturn(Optional.of(disabledEntity));

            assertThatThrownBy(() -> topicService.disableTopic(topicId))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasMessageContaining("already disabled");
        }
    }

    @Nested
    @DisplayName("getPublishedTopics")
    class GetPublishedTopicsTests {

        @Test
        @DisplayName("Should return paginated published topics")
        void shouldReturnPublishedTopics() {
            var pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            var entities = List.of(publishedEntity);
            var page = new PageImpl<>(entities, pageable, 1);

            when(topicRepository.findByStatus(TopicStatus.PUBLISHED, pageable)).thenReturn(page);

            var result = topicService.getPublishedTopics(0, 10);

            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.totalElements()).isEqualTo(1);
            verify(topicRepository).findByStatus(TopicStatus.PUBLISHED, pageable);
        }

        @Test
        @DisplayName("Should return empty page when no published topics")
        void shouldReturnEmptyPage() {
            var pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            var page = new PageImpl<TopicEntity>(List.of(), pageable, 0);

            when(topicRepository.findByStatus(TopicStatus.PUBLISHED, pageable)).thenReturn(page);

            var result = topicService.getPublishedTopics(0, 10);

            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("getTopicsByCreator")
    class GetTopicsByCreatorTests {

        @Test
        @DisplayName("Should return topics by creator")
        void shouldReturnTopicsByCreator() {
            var pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            var entities = List.of(draftEntity);
            var page = new PageImpl<>(entities, pageable, 1);

            when(topicRepository.findByCreatorId(creatorId, pageable)).thenReturn(page);

            var result = topicService.getTopicsByCreator(creatorId, 0, 10);

            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().getFirst().getCreatorId()).isEqualTo(creatorId);
            verify(topicRepository).findByCreatorId(creatorId, pageable);
        }
    }

    @Nested
    @DisplayName("getTopicsByStatus")
    class GetTopicsByStatusTests {

        @Test
        @DisplayName("Should return topics by status")
        void shouldReturnTopicsByStatus() {
            var pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            var entities = List.of(pendingEntity);
            var page = new PageImpl<>(entities, pageable, 1);

            when(topicRepository.findByStatus(TopicStatus.PENDING_APPROVE, pageable)).thenReturn(page);

            var result = topicService.getTopicsByStatus(TopicStatus.PENDING_APPROVE, 0, 10);

            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            verify(topicRepository).findByStatus(TopicStatus.PENDING_APPROVE, pageable);
        }
    }
}
