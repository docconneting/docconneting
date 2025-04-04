package com.example.docconneting.domain.post.controller;

import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.post.dto.PostSingleResponse;
import com.example.docconneting.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
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
}
