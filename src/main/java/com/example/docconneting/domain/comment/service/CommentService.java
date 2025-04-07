package com.example.docconneting.domain.comment.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ServerException;
import com.example.docconneting.domain.comment.dto.request.CommentRequestDto;
import com.example.docconneting.domain.comment.dto.response.CommentResponseDto;
import com.example.docconneting.domain.comment.entity.Comment;
import com.example.docconneting.domain.comment.repository.CommentRepository;
import com.example.docconneting.domain.post.entity.Post;
import com.example.docconneting.domain.post.repository.PostRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponseDto createComment(Long userId, Long postId, CommentRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerException(ErrorCode.USER_NOT_FOUND));

        if (!UserRole.DOCTOR.equals(user.getUserRole())) {
            throw new ServerException(ErrorCode.NOT_ALLOWED_TO_COMMENT);
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ServerException(ErrorCode.POST_NOT_FOUND));

        Comment comment = new Comment(user, post, request.getContents());

        Comment saved = commentRepository.save(comment);

        return CommentResponseDto.builder()
                .id(saved.getId())
                .contents(request.getContents())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    @Transactional
    public CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto request) {

        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(()->new ServerException(ErrorCode.COMMENT_NOT_FOUND));

        validateCommentPermission(userId, findComment);

        Comment createComment = commentRepository.save(findComment);

        return CommentResponseDto.builder()
                .id(createComment.getId())
                .contents(request.getContents())
                .createdAt(findComment.getCreatedAt())
                .updatedAt(findComment.getModifiedAt())
                .build();

    }

    private void validateCommentPermission(Long userId, Comment findComment) {
        if (!userId.equals(findComment.getUser().getId())) {
            throw new ServerException(ErrorCode.NOT_COMMENT_OWNER);
        }
    }
}
