package com.example.docconneting.domain.post.controller;

import com.example.docconneting.domain.post.dto.PostSingleResponse;
import com.example.docconneting.domain.post.service.PostService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    @DisplayName("컨트롤러에서 게시물 단건 조회")
    void findPostByIdTest() throws Exception {
        // given
        Long postId = 1L;

        PostSingleResponse postSingleResponse = PostSingleResponse.builder()
                .id(postId)
                .patientName("patient")
                .title("title")
                .contents("contents")
                .major("major")
                .isReplied(false)
                .deadline(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        given(postService.findPostById(postId)).willReturn(postSingleResponse);

        // when, then
        mockMvc.perform(get("/api/v1/posts/" + postId))
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
    }

    @Test
    @DisplayName("컨트롤러에서 게시물 삭제")
    void deletePostByIdTest() throws Exception {
        // given
        Long postId = 1L;

        doNothing().when(postService).deletePostById(postId);

        // when, then
        mockMvc.perform(delete("/api/v1/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("게시물이 성공적으로 삭제되었습니다."));
    }
}