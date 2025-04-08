package com.example.docconneting.domain.post.controller;

import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.auth.annotation.Auth;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.post.dto.reponse.PostListResponse;
import com.example.docconneting.domain.post.dto.reponse.PostSingleResponse;
import com.example.docconneting.domain.post.dto.reponse.PostUpdateResponse;
import com.example.docconneting.domain.post.dto.request.PostUpdateRequest;
import com.example.docconneting.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostSingleResponse>> findPostById(@PathVariable Long postId){
        return ResponseEntity
                .ok()
                .body(Response.of(postService.findPostById(postId)));
    }

    @GetMapping
    public ResponseEntity<Response<List<PostListResponse>>> findAllPosts(@PageableDefault Pageable pageable,
                                                                         @RequestParam(required = false) String title,
                                                                         @RequestParam(required = false) String major){

        PageResult<PostListResponse> posts = postService.findAllPosts(pageable, title, major);

        return ResponseEntity
                .ok()
                .body(Response.of(posts.getContent(), posts.getPageInfo()));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Response<PostUpdateResponse>> updatePost(@Auth AuthUser authUser, @PathVariable Long postId, @Valid @RequestBody PostUpdateRequest postUpdateRequest){
        return ResponseEntity
                .ok()
                .body(Response.of(postService.updatePost(authUser, postId, postUpdateRequest)));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Response<Map<String, String>>> deletePostById(@Auth AuthUser authUser, @PathVariable Long postId){

        Map<String, String> message = new HashMap<>();
        message.put("message", "게시물이 성공적으로 삭제되었습니다.");

        postService.deletePostById(authUser, postId);

        return ResponseEntity
                .ok()
                .body(Response.of(message));
    }
}
