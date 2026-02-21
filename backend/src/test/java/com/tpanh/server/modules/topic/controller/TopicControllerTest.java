package com.tpanh.server.modules.topic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpanh.server.common.domain.PageResult;
import com.tpanh.server.common.exception.BusinessLogicException;
import com.tpanh.server.common.exception.GlobalExceptionHandler;
import com.tpanh.server.common.exception.ResourceNotFoundException;
import com.tpanh.server.modules.auth.entity.User;
import com.tpanh.server.modules.auth.enums.Role;
import com.tpanh.server.modules.auth.security.CustomUserDetails;
import com.tpanh.server.modules.auth.service.UserService;
import com.tpanh.server.modules.topic.domain.Topic;
import com.tpanh.server.modules.topic.dto.CreateTopicRequest;
import com.tpanh.server.modules.topic.dto.UpdateTopicRequest;
import com.tpanh.server.modules.topic.enums.TopicStatus;
import com.tpanh.server.modules.topic.service.TopicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TopicControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TopicService topicService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TopicController topicController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UUID topicId;
    private UUID creatorId;
    private Topic draftTopic;
    private Topic publishedTopic;
    private User testUser;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(topicController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        topicId = UUID.randomUUID();
        creatorId = UUID.randomUUID();

        testUser = User.builder()
                .id(creatorId)
                .email("test@example.com")
                .fullName("Test User")
                .role(Role.USER)
                .isActive(true)
                .isEmailVerified(true)
                .build();

        userDetails = new CustomUserDetails(testUser);

        draftTopic = Topic.builder()
                .id(topicId)
                .creatorId(creatorId)
                .title("Test Topic")
                .content("Test Content")
                .status(TopicStatus.DRAFT)
                .createdAt(Instant.now())
                .build();

        publishedTopic = Topic.builder()
                .id(topicId)
                .creatorId(creatorId)
                .title("Published Topic")
                .content("Published Content")
                .status(TopicStatus.PUBLISHED)
                .createdAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("GET /api/v1/topics")
    class GetPublishedTopicsTests {

        @Test
        @DisplayName("Should return paginated published topics")
        void shouldReturnPublishedTopics() throws Exception {
            var pageResult = new PageResult<>(List.of(publishedTopic), 0, 10, 1, 1);

            when(topicService.getPublishedTopics(0, 10)).thenReturn(pageResult);
            when(userService.getFullNamesByIds(List.of(creatorId)))
                    .thenReturn(Map.of(creatorId, "Test User"));

            mockMvc.perform(get("/api/v1/topics")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].title").value("Published Topic"))
                    .andExpect(jsonPath("$.content[0].creatorName").value("Test User"))
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        @DisplayName("Should return empty page when no topics")
        void shouldReturnEmptyPage() throws Exception {
            var emptyPage = PageResult.<Topic>empty(0, 10);

            when(topicService.getPublishedTopics(0, 10)).thenReturn(emptyPage);
            when(userService.getFullNamesByIds(List.of()))
                    .thenReturn(Map.of());

            mockMvc.perform(get("/api/v1/topics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        @DisplayName("Should use default page and size params")
        void shouldUseDefaultParams() throws Exception {
            var emptyPage = PageResult.<Topic>empty(0, 10);

            when(topicService.getPublishedTopics(0, 10)).thenReturn(emptyPage);
            when(userService.getFullNamesByIds(List.of()))
                    .thenReturn(Map.of());

            mockMvc.perform(get("/api/v1/topics"))
                    .andExpect(status().isOk());

            verify(topicService).getPublishedTopics(0, 10);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/topics/{id}")
    class GetTopicByIdTests {

        @Test
        @DisplayName("Should return topic detail")
        void shouldReturnTopicDetail() throws Exception {
            when(topicService.getTopicById(topicId)).thenReturn(publishedTopic);
            when(userService.getFullNameById(creatorId)).thenReturn("Test User");

            mockMvc.perform(get("/api/v1/topics/{id}", topicId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(topicId.toString()))
                    .andExpect(jsonPath("$.title").value("Published Topic"))
                    .andExpect(jsonPath("$.content").value("Published Content"))
                    .andExpect(jsonPath("$.creatorName").value("Test User"))
                    .andExpect(jsonPath("$.status").value("PUBLISHED"));
        }

        @Test
        @DisplayName("Should return 404 when topic not found")
        void shouldReturn404WhenNotFound() throws Exception {
            var notFoundId = UUID.randomUUID();
            when(topicService.getTopicById(notFoundId))
                    .thenThrow(new ResourceNotFoundException("Topic", "id", notFoundId));

            mockMvc.perform(get("/api/v1/topics/{id}", notFoundId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/topics")
    class CreateTopicTests {

        @Test
        @DisplayName("Should create topic and return 201")
        void shouldCreateTopic() throws Exception {
            when(topicService.createTopic(any(Topic.class))).thenReturn(draftTopic);

            var requestBody = new CreateTopicRequest("Test Topic", "Test Content");

            mockMvc.perform(post("/api/v1/topics")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody))
                            .principal(() -> "test@example.com")
                            .requestAttr("org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver.AUTHENTICATION_PRINCIPAL", userDetails))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/topics/{id}")
    class UpdateTopicTests {

        @Test
        @DisplayName("Should update topic successfully")
        void shouldUpdateTopic() throws Exception {
            when(topicService.updateTopic(eq(topicId), eq(creatorId), eq("Updated Title"), eq("Updated Content")))
                    .thenReturn(draftTopic);

            var requestBody = new UpdateTopicRequest("Updated Title", "Updated Content");

            mockMvc.perform(put("/api/v1/topics/{id}", topicId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody))
                            .requestAttr("org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver.AUTHENTICATION_PRINCIPAL", userDetails))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 400 when not owner")
        void shouldReturn400WhenNotOwner() throws Exception {
            when(topicService.updateTopic(eq(topicId), eq(creatorId), any(), any()))
                    .thenThrow(new BusinessLogicException("You are not the owner of this topic"));

            var requestBody = new UpdateTopicRequest("Title", "Content");

            mockMvc.perform(put("/api/v1/topics/{id}", topicId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody))
                            .requestAttr("org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver.AUTHENTICATION_PRINCIPAL", userDetails))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/topics/{id}")
    class DeleteTopicTests {

        @Test
        @DisplayName("Should delete topic and return 204")
        void shouldDeleteTopic() throws Exception {
            doNothing().when(topicService).deleteTopic(topicId, creatorId);

            mockMvc.perform(delete("/api/v1/topics/{id}", topicId)
                            .requestAttr("org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver.AUTHENTICATION_PRINCIPAL", userDetails))
                    .andExpect(status().isNoContent());

            verify(topicService).deleteTopic(topicId, creatorId);
        }

        @Test
        @DisplayName("Should return 400 when topic is not deletable")
        void shouldReturn400WhenNotDeletable() throws Exception {
            doThrow(new BusinessLogicException("Topic can only be deleted when in DRAFT status"))
                    .when(topicService).deleteTopic(topicId, creatorId);

            mockMvc.perform(delete("/api/v1/topics/{id}", topicId)
                            .requestAttr("org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver.AUTHENTICATION_PRINCIPAL", userDetails))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/topics/{id}/submit")
    class SubmitForApprovalTests {

        @Test
        @DisplayName("Should submit topic for approval")
        void shouldSubmitForApproval() throws Exception {
            doNothing().when(topicService).submitForApproval(topicId, creatorId);

            mockMvc.perform(post("/api/v1/topics/{id}/submit", topicId)
                            .requestAttr("org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver.AUTHENTICATION_PRINCIPAL", userDetails))
                    .andExpect(status().isOk());

            verify(topicService).submitForApproval(topicId, creatorId);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/topics/{id}/approve")
    class ApproveTopicTests {

        @Test
        @DisplayName("Should approve topic")
        void shouldApproveTopic() throws Exception {
            doNothing().when(topicService).approveTopic(topicId);

            mockMvc.perform(post("/api/v1/topics/{id}/approve", topicId))
                    .andExpect(status().isOk());

            verify(topicService).approveTopic(topicId);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/topics/{id}/reject")
    class RejectTopicTests {

        @Test
        @DisplayName("Should reject topic")
        void shouldRejectTopic() throws Exception {
            doNothing().when(topicService).rejectTopic(topicId);

            mockMvc.perform(post("/api/v1/topics/{id}/reject", topicId))
                    .andExpect(status().isOk());

            verify(topicService).rejectTopic(topicId);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/topics/{id}/archive")
    class ArchiveTopicTests {

        @Test
        @DisplayName("Should archive topic")
        void shouldArchiveTopic() throws Exception {
            doNothing().when(topicService).archiveTopic(topicId);

            mockMvc.perform(post("/api/v1/topics/{id}/archive", topicId))
                    .andExpect(status().isOk());

            verify(topicService).archiveTopic(topicId);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/topics/{id}/disable")
    class DisableTopicTests {

        @Test
        @DisplayName("Should disable topic")
        void shouldDisableTopic() throws Exception {
            doNothing().when(topicService).disableTopic(topicId);

            mockMvc.perform(post("/api/v1/topics/{id}/disable", topicId))
                    .andExpect(status().isOk());

            verify(topicService).disableTopic(topicId);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/topics/status/{status}")
    class GetTopicsByStatusTests {

        @Test
        @DisplayName("Should return topics by status")
        void shouldReturnTopicsByStatus() throws Exception {
            var pageResult = new PageResult<>(List.of(publishedTopic), 0, 10, 1, 1);

            when(topicService.getTopicsByStatus(TopicStatus.PUBLISHED, 0, 10)).thenReturn(pageResult);
            when(userService.getFullNamesByIds(List.of(creatorId)))
                    .thenReturn(Map.of(creatorId, "Test User"));

            mockMvc.perform(get("/api/v1/topics/status/{status}", "PUBLISHED")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].creatorName").value("Test User"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/topics/my")
    class GetMyTopicsTests {

        @Test
        @DisplayName("Should return current user's topics")
        void shouldReturnMyTopics() throws Exception {
            var pageResult = new PageResult<>(List.of(draftTopic), 0, 10, 1, 1);

            when(topicService.getTopicsByCreator(creatorId, 0, 10)).thenReturn(pageResult);

            mockMvc.perform(get("/api/v1/topics/my")
                            .param("page", "0")
                            .param("size", "10")
                            .requestAttr("org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver.AUTHENTICATION_PRINCIPAL", userDetails))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].title").value("Test Topic"));
        }
    }
}
