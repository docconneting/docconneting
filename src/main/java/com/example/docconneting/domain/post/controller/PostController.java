package com.example.docconneting.domain.post.controller;

import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.post.dto.reponse.PostSingleResponse;
import com.example.docconneting.domain.post.dto.reponse.PostUpdateResponse;
import com.example.docconneting.domain.post.dto.request.PostUpdateRequest;
import com.example.docconneting.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    @PatchMapping("/{postId}")
    public ResponseEntity<Response<PostUpdateResponse>> updatePost(@PathVariable Long postId, @Valid @RequestBody PostUpdateRequest postUpdateRequest){
        return ResponseEntity
                .ok()
                .body(Response.of(postService.updatePost(postId, postUpdateRequest)));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Response<Map<String, String>>> deletePostById(@PathVariable Long postId){

        Map<String, String> message = new HashMap<>();
        message.put("message", "게시물이 성공적으로 삭제되었습니다.");

        postService.deletePostById(postId);

        return ResponseEntity
                .ok()
                .body(Response.of(message));
    }
}
