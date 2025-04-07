package com.example.docconneting.domain.post.service;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.post.dto.reponse.PostListResponse;
import com.example.docconneting.domain.post.dto.reponse.PostSingleResponse;
import com.example.docconneting.domain.post.dto.reponse.PostUpdateResponse;
import com.example.docconneting.domain.post.dto.request.PostUpdateRequest;
import com.example.docconneting.domain.post.entity.Post;
import com.example.docconneting.domain.post.repository.PostRepository;
import com.example.docconneting.domain.user.entity.User;
import jakarta.persistence.EntityManager;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    PostRepository postRepository;

    @Mock
    EntityManager entityManager;

    @InjectMocks
    PostService postService;

    @Test
    @DisplayName("서비스에서 게시물 단건 조회시 게시물이 없을 때")
    void findPostByIdFailTest(){
        // given
        Long postId = 1L;

        given(postRepository.findByIdWithUser(postId)).willReturn(Optional.empty());

        // when, then
        ClientException clientException = assertThrows(ClientException.class, () -> postService.findPostById(postId));

        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_POST);

        verify(postRepository, times(1)).findByIdWithUser(postId);
    }

    @Test
    @DisplayName("서비스에서 게시물 단건 조회시 게시물이 이미 삭제된 게시물 일 때")
    void findPostByIdAlreadyDeleteTest(){
        // given
        Long postId = 1L;

        Post post = new Post();
        ReflectionTestUtils.setField(post, "isDeleted", true);

        given(postRepository.findByIdWithUser(postId)).willReturn(Optional.of(post));

        // when, then
        ClientException clientException = assertThrows(ClientException.class, () -> postService.findPostById(postId));

        assertThat(clientException).isInstanceOf(ClientException.class);
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_POST);

        verify(postRepository, times(1)).findByIdWithUser(postId);
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

        given(postRepository.findByIdWithUser(postId)).willReturn(Optional.of(savedPost));

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

        verify(postRepository, times(1)).findByIdWithUser(postId);
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

        verify(postRepository, times(1)).findById(postId);
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

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    @DisplayName("서비스에서 게시물 수정시 게시물이 없을 때")
    void updatePostFailedTest(){
        // given
        Long postId = 1L;

        PostUpdateRequest postUpdateRequest = new PostUpdateRequest();
        ReflectionTestUtils.setField(postUpdateRequest, "title", "tile");
        ReflectionTestUtils.setField(postUpdateRequest, "contents", "content");

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when, then
        ClientException clientException = assertThrows(ClientException.class, () -> postService.updatePost(postId, postUpdateRequest));

        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_POST);

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    @DisplayName("서비스에서 게시물 수정시 게시물이 삭제된 게시물 일 때")
    void updatePostAlreadyDeletedTest(){
        // given
        Long postId = 1L;

        Post post = new Post();
        ReflectionTestUtils.setField(post, "isDeleted", true);

        PostUpdateRequest postUpdateRequest = new PostUpdateRequest();
        ReflectionTestUtils.setField(postUpdateRequest, "title", "tile");
        ReflectionTestUtils.setField(postUpdateRequest, "contents", "content");

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when, then
        ClientException clientException = assertThrows(ClientException.class, () -> postService.updatePost(postId, postUpdateRequest));

        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_POST);

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    @DisplayName("서비스에서 게시물 수정 테스트")
    void updatePostTest(){
        // given
        Long postId = 1L;

        Post post = new Post();
        ReflectionTestUtils.setField(post, "isDeleted", false);
        ReflectionTestUtils.setField(post, "id", postId);
        ReflectionTestUtils.setField(post, "title", "title");
        ReflectionTestUtils.setField(post, "contents", "content");
        ReflectionTestUtils.setField(post, "major", Major.ORTHOPEDICS);
        ReflectionTestUtils.setField(post, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(post, "modifiedAt", LocalDateTime.now());

        PostUpdateRequest postUpdateRequest = new PostUpdateRequest();
        ReflectionTestUtils.setField(postUpdateRequest, "title", "updateTitle");
        ReflectionTestUtils.setField(postUpdateRequest, "contents", "updateContent");

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        doNothing().when(entityManager).flush();

        // when
        PostUpdateResponse postUpdateResponse = postService.updatePost(postId, postUpdateRequest);

        // then
        assertThat(postUpdateResponse.getId()).isEqualTo(postId);
        assertThat(postUpdateResponse.getTitle()).isEqualTo(postUpdateRequest.getTitle());
        assertThat(postUpdateResponse.getContents()).isEqualTo(postUpdateRequest.getContents());
        assertThat(postUpdateResponse.getMajor()).isEqualTo(Major.ORTHOPEDICS.name());
        assertThat(postUpdateResponse.getCreatedAt()).isEqualTo(post.getCreatedAt());
        assertThat(postUpdateResponse.getModifiedAt()).isEqualTo(post.getModifiedAt());

        verify(postRepository, times(1)).findById(postId);
        verify(entityManager, times(1)).flush();
    }

    @Test
    @DisplayName("서비스에서 게시물 리스트 조회")
    void findAllPostsTest(){
        // given
        User user = new User();
        ReflectionTestUtils.setField(user, "username", "testName");

        Pageable pageable = PageRequest.of(0, 10);
        String title = "title";
        String major = Major.values()[0].name();

        List<Post> content = new ArrayList<>();
        for(int i=0;i<50;i++){
            Post post = Post.of(user, title, "contents", Major.valueOf(major), false, false, false, LocalDateTime.now());
            content.add(post);
        }

        Page<Post> posts = new PageImpl<>(content, pageable, content.size());

        given(postRepository.findPosts(pageable, title, major)).willReturn(posts);

        // when
        PageResult<PostListResponse> pageResult = postService.findAllPosts(pageable, title, major);

        List<PostListResponse> getContent = pageResult.getContent();
        PageInfo pageInfo = pageResult.getPageInfo();

        // then
        assertThat(getContent.size()).isEqualTo(content.size());
        assertThat(pageInfo.getPageNum()).isEqualTo(pageable.getPageNumber());
        assertThat(pageInfo.getPageSize()).isEqualTo(pageable.getPageSize());
        assertThat(pageInfo.getTotalPage()).isEqualTo(posts.getTotalPages());
        assertThat(pageInfo.getTotalElement()).isEqualTo(posts.getTotalElements());

        verify(postRepository, times(1)).findPosts(pageable, title, major);
    }
}