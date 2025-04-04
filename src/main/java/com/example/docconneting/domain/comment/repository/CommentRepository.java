package com.example.docconneting.domain.comment.repository;

import com.example.docconneting.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
