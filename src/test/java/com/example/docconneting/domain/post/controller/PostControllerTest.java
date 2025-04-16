package com.example.docconneting.domain.post.controller;

import com.example.docconneting.common.config.JwtUtil;
import com.example.docconneting.common.enums.Major;
import com.example.docconneting.common.filter.JwtFilter;
import com.example.docconneting.common.resolver.AuthUserArgumentResolver;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.post.dto.reponse.PostListResponse;
import com.example.docconneting.domain.post.dto.reponse.PostSingleResponse;
import com.example.docconneting.domain.post.dto.reponse.PostUpdateResponse;
import com.example.docconneting.domain.post.dto.request.PostUpdateRequest;
import com.example.docconneting.domain.post.entity.Post;
import com.example.docconneting.domain.post.service.PostService;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PostController.class)
@TestPropertySource(properties = {
        "jwt.secret.key=5Gk6hibHDtKLFVk4NdBX039rvehSLNjfKsdXpm/pHsU="
})
@Import({JwtUtil.class, AuthUserArgumentResolver.class, JwtFilter.class})
class PostControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtUtil jwtUtil;

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
        Long userId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        String accessToken = jwtUtil.createToken(userId, userRole);

        Long postId = 1L;

        doNothing().when(postService).deletePostById(refEq(authUser), eq(postId));

        // when, then
        mockMvc.perform(delete("/api/v1/posts/{postId}", postId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("게시물이 성공적으로 삭제되었습니다."));

        verify(postService, times(1)).deletePostById(refEq(authUser), eq(postId));
    }

    @Test
    @DisplayName("컨트롤러에서 게시물 수정")
    void updatePostTest() throws Exception{
        // given
        Long userId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        String accessToken = jwtUtil.createToken(userId, userRole);

        Long postId = 1L;

        PostUpdateRequest postUpdateRequest = new PostUpdateRequest();
        ReflectionTestUtils.setField(postUpdateRequest, "title", "updateTitle");
        ReflectionTestUtils.setField(postUpdateRequest, "contents", "updateContent");

        String postUpdateRequestJson = objectMapper.writeValueAsString(postUpdateRequest);

        PostUpdateResponse postUpdateResponse = PostUpdateResponse.of(postId, "updateTitle", "updateContent", Major.ORTHOPEDICS.name(), LocalDateTime.now(), LocalDateTime.now());

        given(postService.updatePost(refEq(authUser), eq(postId), refEq(postUpdateRequest))).willReturn(postUpdateResponse);

        // when, then
        mockMvc.perform(patch("/api/v1/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postUpdateRequestJson)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(postId))
                .andExpect(jsonPath("$.data.title").value(postUpdateResponse.getTitle()))
                .andExpect(jsonPath("$.data.contents").value(postUpdateResponse.getContents()))
                .andExpect(jsonPath("$.data.major").value(postUpdateResponse.getMajor()))
                .andExpect(jsonPath("$.data.createdAt", Matchers.startsWith(postUpdateResponse.getCreatedAt().toString().substring(0,19))))
                .andExpect(jsonPath("$.data.modifiedAt", Matchers.startsWith(postUpdateResponse.getModifiedAt().toString().substring(0,19))));

        verify(postService, times(1)).updatePost(refEq(authUser), eq(postId), refEq(postUpdateRequest));
    }

    @Test
    @DisplayName("컨트롤러에서 게시물 리스트 조회")
    void findAllPostsTest() throws Exception {
        // given
        User user = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(user, "username", "testName");

        Pageable pageable = PageRequest.of(0, 10);
        String title = "title";
        String major = Major.values()[0].name();

        List<Post> posts = new ArrayList<>();
        for(int i = 0; i < pageable.getPageSize(); i++){
            Post post = Post.of(user, title, "contents", Major.valueOf(major), false, false);
            posts.add(post);
        }

        List<PostListResponse> postListResponses = PostListResponse.toPostListResponses(posts);

        PageInfo pageInfo = PageInfo.builder()
                .pageNum(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElement( postListResponses.size())
                .totalPage(postListResponses.size()/pageable.getPageSize())
                .build();

        PageResult<PostListResponse> pageResult = new PageResult<>(postListResponses, pageInfo);

        given(postService.findAllPosts(argThat(
                p -> p.getPageNumber() == pageable.getPageNumber() && p.getPageSize() == pageable.getPageSize()
        ), eq(title), eq(major))).willReturn(pageResult);

        // when, then
        mockMvc.perform(get("/api/v1/posts").param("title", title).param("major", major))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.pageNum").value(pageInfo.getPageNum()))
                .andExpect(jsonPath("$.page.pageSize").value(pageInfo.getPageSize()))
                .andExpect(jsonPath("$.page.totalElement").value(pageInfo.getTotalElement()))
                .andExpect(jsonPath("$.page.totalPage").value(pageInfo.getTotalPage()))
                .andExpect(jsonPath("$.data.size()").value(postListResponses.size()));

        verify(postService, times(1)).findAllPosts(argThat(
                p -> p.getPageNumber() == pageable.getPageNumber() && p.getPageSize() == pageable.getPageSize()
        ), eq(title), eq(major));
    }
}