package com.example.docconneting.domain.comment.repository;

import com.example.docconneting.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c where c.post.id = :postId")
    Page<Comment> findPosts(@Param("postId") Long postId, Pageable pageable);
}
