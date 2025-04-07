package com.example.docconneting.domain.comment.controller;

import com.example.docconneting.domain.comment.dto.request.CommentRequestDto;
import com.example.docconneting.domain.comment.dto.response.CommentResponseDto;
import com.example.docconneting.domain.comment.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    private CommentService commentService;

    @Test
    void 댓글_작성_성공() throws Exception {
        // given
        Long userId = 1L;
        Long postId = 100L;
        Long commentId = 500L;
        String content = "comments";
        LocalDateTime now = LocalDateTime.now();

        CommentRequestDto request = new CommentRequestDto(content);
        CommentResponseDto response = new CommentResponseDto(commentId, content, now, now);

        given(commentService.createComment(userId, postId, request))
                .willReturn(response);

        mockMvc.perform(post("/api/v1/posts/{postId}/comments",postId)
                .header("userId", String.valueOf(userId))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(commentId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(content));
    }

    @Test
    void 댓글_수정_성공() throws Exception {
        // given
        Long userId = 1L;
        Long postId = 100L;
        Long commentId = 500L;
        String updateContent = "updateComments";
        LocalDateTime now = LocalDateTime.now();

        CommentRequestDto request = new CommentRequestDto(updateContent);
        CommentResponseDto response = new CommentResponseDto(commentId, updateContent, now, now);

        given(commentService.updateComment(userId, postId, request))
        .willReturn(response);

        mockMvc.perform(patch("/api/v1/posts/{postId}/comments/{commentId}", postId, commentId)
                .header("userId", String.valueOf(userId))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(commentId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(updateContent));

    }



}