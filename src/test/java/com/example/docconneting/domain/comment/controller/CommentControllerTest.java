package com.example.docconneting.domain.comment.controller;

import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.Auth.annotation.Auth;
import com.example.docconneting.domain.comment.dto.response.CommentListResponse;
import com.example.docconneting.domain.comment.entity.Comment;
import com.example.docconneting.domain.comment.service.CommentService;
import com.example.docconneting.domain.post.dto.reponse.PostListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CommentService commentService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("컨트롤러에서 게시물 댓글 리스트 조회")
    void findAllCommentsTest() throws Exception {
        // given
        Long postId = 1L;

        Pageable pageable = PageRequest.of(0,10);

        List<Comment> comments = new ArrayList<>();
        for(int i=0;i<50;i++){
            Comment comment = new Comment();
            comments.add(comment);
        }

        List<CommentListResponse> content = CommentListResponse.toCommentListResponses(comments);

        PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(), content.size(), content.size()/pageable.getPageSize());

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