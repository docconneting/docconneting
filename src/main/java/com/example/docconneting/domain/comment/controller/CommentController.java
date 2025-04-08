package com.example.docconneting.domain.comment.controller;

import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.Auth.annotation.Auth;
import com.example.docconneting.domain.Auth.entity.AuthUser;
import com.example.docconneting.domain.comment.dto.request.CommentRequest;
import com.example.docconneting.domain.comment.dto.response.CommentResponse;
import com.example.docconneting.domain.comment.dto.response.CommentListResponse;
import com.example.docconneting.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Response<CommentResponse>> createComment(
            @Auth AuthUser authUser,
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request
    ) {
        Long userId = authUser.getId();
        return new ResponseEntity<>(Response.of(commentService.createComment(userId, postId, request)), HttpStatus.CREATED);
    }

    @PatchMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Response<CommentResponse>> updateComment(
        @Auth AuthUser authUser,
        @PathVariable Long commentId,
        @Valid @RequestBody CommentRequest request
    ) {
        Long userId = authUser.getId();
        return new ResponseEntity<>(Response.of(commentService.updateComment(userId, commentId, request)), HttpStatus.OK);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<Response<List<CommentListResponse>>> findAllComments(@PathVariable Long postId, @PageableDefault Pageable pageable){
        PageResult<CommentListResponse> comments = commentService.findAllComments(postId, pageable);

        return ResponseEntity
                .ok()
                .body(Response.of(comments.getContent(), comments.getPageInfo()));
    }
}
