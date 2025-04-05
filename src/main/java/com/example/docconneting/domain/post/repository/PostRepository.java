package com.example.docconneting.domain.post.repository;

import com.example.docconneting.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends PostRepositoryCustom, JpaRepository<Post, Long> {
    @Query("select p from Post p join fetch p.patient where p.id = :userId")
    Optional<Post> findByIdWithUser(@Param(value = "userId") Long userId);
}
