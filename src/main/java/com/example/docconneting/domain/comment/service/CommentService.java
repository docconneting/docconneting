package com.example.docconneting.domain.comment.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.comment.dto.response.CommentListResponse;
import com.example.docconneting.domain.comment.entity.Comment;
import com.example.docconneting.domain.comment.repository.CommentRepository;
import com.example.docconneting.domain.post.entity.Post;
import com.example.docconneting.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public PageResult<CommentListResponse> findAllComments(Long postId, Pageable pageable){

        Post findPost = postRepository.findById(postId).orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_POST));

        if(findPost.getIsDeleted()){
            throw new ClientException(ErrorCode.NOT_FOUND_POST);
        }

        Page<Comment> posts = commentRepository.findPosts(postId, pageable);

        List<Comment> content = posts.getContent();
        Pageable commentsPageable = posts.getPageable();

        List<CommentListResponse> commentListResponses = CommentListResponse.toCommentListResponses(content);

        PageInfo pageInfo = new PageInfo(commentsPageable.getPageNumber(), commentsPageable.getPageSize(), posts.getTotalElements(), posts.getTotalPages());

        return new PageResult<>(commentListResponses, pageInfo);
    }
}
