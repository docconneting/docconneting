package com.example.docconneting.domain.comment.controller;

import com.example.docconneting.domain.comment.dto.request.CommentRequestDto;
import com.example.docconneting.domain.comment.dto.response.CommentResponseDto;
import com.example.docconneting.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @RequestHeader Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequestDto request
    ) {
        return new ResponseEntity<>(commentService.createComment(userId, postId, request), HttpStatus.CREATED);
    }

    @PatchMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
        @RequestHeader Long userId,
        @PathVariable Long postId,
        @PathVariable Long commentId,
        @Valid @RequestBody CommentRequestDto request
    ) {
        return new ResponseEntity<>(commentService.updateComment(userId, commentId, request), HttpStatus.OK);
    }
}
