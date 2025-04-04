package com.example.docconneting.domain.post.service;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.post.dto.reponse.PostSingleResponse;
import com.example.docconneting.domain.post.entity.Post;
import com.example.docconneting.domain.post.repository.PostRepository;
import com.example.docconneting.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    PostRepository postRepository;

    @InjectMocks
    PostService postService;

    @Test
    @DisplayName("서비스에서 게시물 단건 조회시 게시물이 없을 때")
    void findPostByIdFailTest(){
        // given
        Long postId = 1L;

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when, then
        ClientException clientException = assertThrows(ClientException.class, () -> postService.findPostById(postId));

        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_POST);
    }

    @Test
    @DisplayName("서비스에서 게시물 단건 조회시 게시물이 이미 삭제된 게시물 일 때")
    void findPostByIdAlreadyDeleteTest(){
        // given
        Long postId = 1L;

        Post post = new Post();
        ReflectionTestUtils.setField(post, "isDeleted", true);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when, then
        ClientException clientException = assertThrows(ClientException.class, () -> postService.findPostById(postId));

        assertThat(clientException).isInstanceOf(ClientException.class);
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_POST);
    }

    @Test
    @DisplayName("서비스에서 게시물 단건 조회 성공")
    void findPostByIdSuccessTest(){
        // given
        Long postId = 1L;

        User user = new User();
        ReflectionTestUtils.setField(user, "username", "username");

        Post savedPost = new Post();
        ReflectionTestUtils.setField(savedPost, "id", postId);
        ReflectionTestUtils.setField(savedPost, "patient", user);
        ReflectionTestUtils.setField(savedPost, "title", "title");
        ReflectionTestUtils.setField(savedPost, "contents", "content");
        ReflectionTestUtils.setField(savedPost, "major", Major.ORTHOPEDICS);
        ReflectionTestUtils.setField(savedPost, "isDeleted", false);
        ReflectionTestUtils.setField(savedPost, "isReplied", false);
        ReflectionTestUtils.setField(savedPost, "deadline", LocalDateTime.now());
        ReflectionTestUtils.setField(savedPost, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(savedPost, "modifiedAt", LocalDateTime.now());

        given(postRepository.findById(postId)).willReturn(Optional.of(savedPost));

        // when
        PostSingleResponse postSingleResponse = postService.findPostById(postId);

        // then
        assertThat(postSingleResponse.getId()).isEqualTo(postId);
        assertThat(savedPost.getPatient().getUsername()).isEqualTo(postSingleResponse.getPatientName());
        assertThat(postSingleResponse.getTitle()).isEqualTo("title");
        assertThat(postSingleResponse.getContents()).isEqualTo("content");
        assertThat(postSingleResponse.getMajor()).isEqualTo(Major.ORTHOPEDICS.name());
        assertThat(postSingleResponse.getIsReplied()).isEqualTo(false);
        assertThat(postSingleResponse.getDeadline()).isEqualTo(savedPost.getDeadline());
        assertThat(postSingleResponse.getCreatedAt()).isEqualTo(savedPost.getCreatedAt());
        assertThat(postSingleResponse.getModifiedAt()).isEqualTo(savedPost.getModifiedAt());
    }

    @Test
    @DisplayName("서비스에서 게시물 삭제시 게시물이 없을 떄")
    void deletePostByIdFailTest(){
        // given
        Long postId = 1L;

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when, then
        ClientException clientException = assertThrows(ClientException.class, () -> postService.deletePostById(postId));

        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_POST);
    }

    @Test
    @DisplayName("서비스에서 게시물 삭제 테스트")
    void deletePostByIdSuccessTest(){
        // given
        Long postId = 1L;

        Post post = new Post();
        ReflectionTestUtils.setField(post, "isDeleted", false);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when, then
        postService.deletePostById(postId);

        assertThat(post.getIsDeleted()).isTrue();
    }
}