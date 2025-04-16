package com.example.docconneting.domain.comment.controller;

import com.example.docconneting.common.config.JwtUtil;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.comment.dto.request.CommentRequest;
import com.example.docconneting.domain.comment.dto.response.CommentListResponse;
import com.example.docconneting.domain.comment.dto.response.CommentResponse;
import com.example.docconneting.domain.comment.entity.Comment;
import com.example.docconneting.domain.comment.service.CommentService;
import com.example.docconneting.domain.user.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

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
        String contents = "comments";
        LocalDateTime now = LocalDateTime.now();

        String accessToken = jwtUtil.createToken(userId, UserRole.DOCTOR);

        CommentRequest request = new CommentRequest();
        ReflectionTestUtils.setField(request, "contents", "comments");

        CommentResponse response = CommentResponse.of(commentId, contents, now, now);

        given(commentService.createComment(eq(userId), eq(postId), any(CommentRequest.class)))
                .willReturn(response);

        mockMvc.perform(post("/api/v1/posts/{postId}/comments",postId)
                        .header("Authorization", accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(commentId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.contents").value(contents));
    }

    @Test
    void 댓글_수정_성공() throws Exception {
        // given
        Long userId = 1L;
        Long postId = 100L;
        Long commentId = 500L;
        String updateContent = "updateComments";
        LocalDateTime now = LocalDateTime.now();

        String accessToken = jwtUtil.createToken(userId, UserRole.DOCTOR);

        CommentRequest request = new CommentRequest();
        ReflectionTestUtils.setField(request, "contents", "updateComments");

        CommentResponse response = CommentResponse.of(commentId, updateContent, now, now);

        given(commentService.updateComment(eq(userId), eq(commentId), any(CommentRequest.class)))
                .willReturn(response);

        mockMvc.perform(patch("/api/v1/posts/{postId}/comments/{commentId}", userId, commentId)
                        .header("Authorization", accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(commentId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.contents").value(updateContent));
    }

    @Test
    @DisplayName("컨트롤러에서 게시물 댓글 리스트 조회")
    void findAllCommentsTest() throws Exception {
        // given
        Long postId = 1L;

        Pageable pageable = PageRequest.of(0,10);

        List<Comment> comments = new ArrayList<>();
        for(int i = 0; i < 50; i++){
            Comment comment = Comment.of(null, null, null);
            comments.add(comment);
        }

        List<CommentListResponse> content = CommentListResponse.toCommentListResponses(comments);

        PageInfo pageInfo = PageInfo.builder()
                .pageNum(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElement(content.size())
                .totalPage(content.size()/pageable.getPageSize())
                .build();

        PageResult<CommentListResponse> pageResult = new PageResult<>(content, pageInfo);

        given(commentService.findAllComments(any(Long.class), any(Pageable.class))).willReturn(pageResult);

        // when, then
        mockMvc.perform(get("/api/v1/posts/{postsId}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.pageNum").value(pageInfo.getPageNum()))
                .andExpect(jsonPath("$.page.pageSize").value(pageInfo.getPageSize()))
                .andExpect(jsonPath("$.page.totalElement").value(pageInfo.getTotalElement()))
                .andExpect(jsonPath("$.page.totalPage").value(pageInfo.getTotalPage()))
                .andExpect(jsonPath("$.data.size()").value(content.size()));
    }
}