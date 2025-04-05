package com.example.docconneting.domain.post.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.post.dto.reponse.PostListResponse;
import com.example.docconneting.domain.post.dto.reponse.PostSingleResponse;
import com.example.docconneting.domain.post.dto.reponse.PostUpdateResponse;
import com.example.docconneting.domain.post.dto.request.PostUpdateRequest;
import com.example.docconneting.domain.post.entity.Post;
import com.example.docconneting.domain.post.repository.PostRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final EntityManager entityManager;

    // 게시물 단건 조회
    @Transactional(readOnly = true)
    public PostSingleResponse findPostById(Long postId){
        Post findPost = postRepository.findByIdWithUser(postId).orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_POST));

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

    @Transactional(readOnly = true)
    public PageResult<PostListResponse> findAllPosts(Pageable pageable, String title, String major){

        Page<Post> posts = postRepository.findPosts(pageable, title, major);

        List<Post> content = posts.getContent();
        Pageable postsPageable = posts.getPageable();

        List<PostListResponse> postsListResponses = content.stream().map(post -> PostListResponse.builder()
                        .id(post.getId())
                        .patientName(post.getPatient().getUsername())
                        .title(post.getTitle())
                        .contents(post.getContents())
                        .major(post.getMajor().name())
                        .isReplied(post.getIsReplied())
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build())
                .collect(Collectors.toList());

        PageInfo pageInfo = new PageInfo(postsPageable.getPageNumber(), postsPageable.getPageSize(), posts.getTotalElements(), posts.getTotalPages());

        return new PageResult<>(postsListResponses, pageInfo);
    }

    // 게시물 수정
    @Transactional
    public PostUpdateResponse updatePost(Long postId, PostUpdateRequest postUpdateRequest){
        Post findPost = postRepository.findById(postId).orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_POST));

        if(findPost.getIsDeleted()){
            throw new ClientException(ErrorCode.NOT_FOUND_POST);
        }

        findPost.updateTitle(postUpdateRequest.getTitle());
        findPost.updateContents(postUpdateRequest.getContents());

        // flush() 시점에 @LastModifiedDate 이 작동한다
        entityManager.flush();

        return PostUpdateResponse.builder()
                .id(findPost.getId())
                .title(findPost.getTitle())
                .contents(findPost.getContents())
                .major(findPost.getMajor().name())
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
