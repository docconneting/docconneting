package com.example.docconneting.domain.comment.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.comment.dto.response.CommentListResponse;
import com.example.docconneting.domain.comment.entity.Comment;
import com.example.docconneting.domain.comment.repository.CommentRepository;
import com.example.docconneting.domain.post.dto.reponse.PostListResponse;
import com.example.docconneting.domain.post.entity.Post;
import com.example.docconneting.domain.post.repository.PostRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    PostRepository postRepository;

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentService commentService;

    @Test
    @DisplayName("댓글 리스트 조회 게시물이 존재하지 않을 때")
    void findAllCommentsNoPostTest(){
        // given
        Long postId = 1L;

        Pageable pageable = PageRequest.of(0,10);

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> commentService.findAllComments(postId, pageable));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_POST);

        verify(postRepository, times(1)).findById(postId);
        verify(commentRepository, times(0)).findPosts(postId, pageable);
    }

    @Test
    @DisplayName("댓글 리스트 조회 게시물이 삭제된 게시물 일 때")
    void findAllCommentsDeletedPostTest(){
        // given
        Long postId = 1L;

        Pageable pageable = PageRequest.of(0,10);

        Post post = new Post();
        ReflectionTestUtils.setField(post, "isDeleted", true);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> commentService.findAllComments(postId, pageable));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_POST);

        verify(postRepository, times(1)).findById(postId);
        verify(commentRepository, times(0)).findPosts(postId, pageable);
    }

    @Test
    @DisplayName("댓글 리스트 조회")
    void findAllCommentsTest(){
        // given
        Long postId = 1L;

        Pageable pageable = PageRequest.of(0,10);

        List<Comment> content = new ArrayList<>();
        for(int i=0;i<50;i++){
            Comment comment = new Comment();
            content.add(comment);
        }

        Page<Comment> page = new PageImpl<>(content, pageable, content.size());

        Post post = new Post();
        ReflectionTestUtils.setField(post, "isDeleted", false);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        given(commentRepository.findPosts(postId, pageable)).willReturn(page);

        // when
        PageResult<CommentListResponse> pageResult = commentService.findAllComments(postId, pageable);

        List<CommentListResponse> getContent = pageResult.getContent();
        PageInfo pageInfo = pageResult.getPageInfo();

        // then
        assertThat(getContent.size()).isEqualTo(content.size());
        assertThat(pageInfo.getPageNum()).isEqualTo(pageable.getPageNumber());
        assertThat(pageInfo.getPageSize()).isEqualTo(pageable.getPageSize());
        assertThat(pageInfo.getTotalPage()).isEqualTo(page.getTotalPages());
        assertThat(pageInfo.getTotalElement()).isEqualTo(page.getTotalElements());

        verify(postRepository, times(1)).findById(postId);
        verify(commentRepository, times(1)).findPosts(postId, pageable);
    }
}