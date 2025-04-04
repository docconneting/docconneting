package com.example.docconneting.domain.comment.entity;

import com.example.docconneting.common.base.BaseEntity;
import com.example.docconneting.domain.post.entity.Post;
import com.example.docconneting.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor
public class Comment extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String contents;

    public Comment(User user, Post post, String contents) {
        this.user = user;
        this.post = post;
        this.contents = contents;
    }

    public void updateContents(String contents) {
        this.contents = contents;
    }
}
