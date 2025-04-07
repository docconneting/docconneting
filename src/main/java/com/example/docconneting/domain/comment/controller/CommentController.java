package com.example.docconneting.domain.comment.controller;

import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.comment.dto.response.CommentListResponse;
import com.example.docconneting.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<Response<List<CommentListResponse>>> findAllComments(@PathVariable Long postId, @PageableDefault Pageable pageable){
        PageResult<CommentListResponse> comments = commentService.findAllComments(postId, pageable);

        return ResponseEntity
                .ok()
                .body(Response.of(comments.getContent(), comments.getPageInfo()));
    }
}
