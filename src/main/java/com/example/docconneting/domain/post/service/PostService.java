package com.example.docconneting.domain.post.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.post.dto.PostSingleResponse;
import com.example.docconneting.domain.post.entity.Post;
import com.example.docconneting.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    // 게시물 단건 조회
    @Transactional(readOnly = true)
    public PostSingleResponse findPostById(Long postId){
        Post findPost = postRepository.findById(postId).orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_POST));

        if(findPost.getIsDeleted()){
            throw new ClientException(ErrorCode.NOT_FOUND_POST);
        }

        return PostSingleResponse.builder()
                .id(findPost.getId())
                .patientName(findPost.getPatient().getUsername())
                .title(findPost.getTitle())
                .contents(findPost.getContents())
                .major(findPost.getMajor().name())
                .isReplied(findPost.getIsReplied())
                .deadline(findPost.getDeadline())
                .createdAt(findPost.getCreatedAt())
                .modifiedAt(findPost.getModifiedAt())
                .build();
    }

    // 게시물 삭제
    @Transactional
    public void deletePostById(Long postId){
        Post findPost = postRepository.findById(postId).orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_POST));
        findPost.delete();
    }
}
