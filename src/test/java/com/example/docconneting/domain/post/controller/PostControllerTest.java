package com.example.docconneting.domain.post.controller;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.domain.post.dto.reponse.PostSingleResponse;
import com.example.docconneting.domain.post.dto.reponse.PostUpdateResponse;
import com.example.docconneting.domain.post.dto.request.PostUpdateRequest;
import com.example.docconneting.domain.post.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.asm.Advice;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PostController.class)
class PostControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("컨트롤러에서 게시물 단건 조회")
    void findPostByIdTest() throws Exception {
        // given
        Long postId = 1L;

        PostSingleResponse postSingleResponse = PostSingleResponse.of(postId, "patient", "title", "contents", "major", false, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

        given(postService.findPostById(postId)).willReturn(postSingleResponse);

        // when, then
        mockMvc.perform(get("/api/v1/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(postId))
                .andExpect(jsonPath("$.data.patientName").value(postSingleResponse.getPatientName()))
                .andExpect(jsonPath("$.data.title").value(postSingleResponse.getTitle()))
                .andExpect(jsonPath("$.data.contents").value(postSingleResponse.getContents()))
                .andExpect(jsonPath("$.data.major").value(postSingleResponse.getMajor()))
                .andExpect(jsonPath("$.data.isReplied").value(postSingleResponse.getIsReplied()))
                .andExpect(jsonPath("$.data.deadline", Matchers.startsWith(postSingleResponse.getDeadline().toString().substring(0,19))))
                .andExpect(jsonPath("$.data.createdAt", Matchers.startsWith(postSingleResponse.getCreatedAt().toString().substring(0,19))))
                .andExpect(jsonPath("$.data.modifiedAt", Matchers.startsWith(postSingleResponse.getModifiedAt().toString().substring(0,19))));

        verify(postService, times(1)).findPostById(postId);
    }

    @Test
    @DisplayName("컨트롤러에서 게시물 삭제")
    void deletePostByIdTest() throws Exception {
        // given
        Long postId = 1L;

        doNothing().when(postService).deletePostById(postId);

        // when, then
        mockMvc.perform(delete("/api/v1/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("게시물이 성공적으로 삭제되었습니다."));

        verify(postService, times(1)).deletePostById(postId);
    }

    @Test
    @DisplayName("컨트롤러에서 게시물 수정")
    void updatePostTest() throws Exception{
        // given
        Long postId = 1L;

        PostUpdateRequest postUpdateRequest = new PostUpdateRequest();
        ReflectionTestUtils.setField(postUpdateRequest, "title", "updateTitle");
        ReflectionTestUtils.setField(postUpdateRequest, "contents", "updateContent");

        String postUpdateRequestJson = objectMapper.writeValueAsString(postUpdateRequest);

        PostUpdateResponse postUpdateResponse = PostUpdateResponse.of(postId, "updateTitle", "updateContent", Major.ORTHOPEDICS.name(), LocalDateTime.now(), LocalDateTime.now());

        given(postService.updatePost(any(Long.class), any(PostUpdateRequest.class))).willReturn(postUpdateResponse);

        // when, then
        mockMvc.perform(patch("/api/v1/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postUpdateRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(postId))
                .andExpect(jsonPath("$.data.title").value(postUpdateResponse.getTitle()))
                .andExpect(jsonPath("$.data.contents").value(postUpdateResponse.getContents()))
                .andExpect(jsonPath("$.data.major").value(postUpdateResponse.getMajor()))
                .andExpect(jsonPath("$.data.createdAt", Matchers.startsWith(postUpdateResponse.getCreatedAt().toString().substring(0,19))))
                .andExpect(jsonPath("$.data.modifiedAt", Matchers.startsWith(postUpdateResponse.getModifiedAt().toString().substring(0,19))));

        verify(postService, times(1)).updatePost(any(Long.class), any(PostUpdateRequest.class));
    }
}