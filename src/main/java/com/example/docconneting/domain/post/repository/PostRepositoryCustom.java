package com.example.docconneting.domain.post.repository;

import com.example.docconneting.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<Post> findPosts(Pageable pageable, String title, String major);
}
