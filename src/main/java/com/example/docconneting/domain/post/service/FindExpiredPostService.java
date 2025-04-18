package com.example.docconneting.domain.post.service;

import com.example.docconneting.domain.post.entity.Post;
import com.example.docconneting.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FindExpiredPostService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public List<Post> findExpiredPosts() {
        LocalDateTime now = LocalDateTime.now();
        return postRepository.findAllByDeadlineBeforeAndPayTypeCouponOrPayTypePoint(now);
    }
}
