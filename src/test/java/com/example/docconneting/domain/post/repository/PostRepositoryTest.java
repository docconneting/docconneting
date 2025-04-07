package com.example.docconneting.domain.post.repository;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.domain.post.entity.Post;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class PostRepositoryTest {
    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void createPosts(){
        User user = new User();
        User savedUser = userRepository.save(user);
        Random random = new Random(System.currentTimeMillis());
        List<Post> posts = new ArrayList<>();
        for(int i=0;i<10000;i++){
            int randomNumber = random.nextInt(5);
            Major[] majors = Major.values();
            Post post = Post.of(savedUser, "title"+(i+1), "contents"+(i+1), majors[randomNumber], false, false, false, LocalDateTime.now());
            posts.add(post);
        }
        postRepository.saveAll(posts);
    }

    @BeforeEach
    @Transactional
    void setData(){
        User user = new User();
        User savedUser = userRepository.save(user);

        User findUser = userRepository.findById(savedUser.getId()).get();

        Random random = new Random(System.currentTimeMillis());
        List<Post> posts = new ArrayList<>();
        for(int i=0;i<50;i++){
            Major major = Major.values()[i % 5];
            String title = "title" + (i % 5);
            Post post = Post.of(findUser, title, "contents"+(i+1), major, false, false, false, LocalDateTime.now());
            posts.add(post);
        }
        postRepository.saveAll(posts);
    }

    @Test
    @Transactional
    void findAllPostsTest(){
        // given
        Pageable pageable = PageRequest.of(0,10);
        String title = null;
        String major = null;

        // when
        Page<Post> posts = postRepository.findPosts(pageable, title, major);

        // then
        assertThat(posts.getTotalElements()).isEqualTo(50);
        assertThat(posts.getTotalPages()).isEqualTo(5);
        assertThat(posts.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(posts.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @Transactional
    void findAllPostsByMajorTest(){
        // given
        Pageable pageable = PageRequest.of(0,10);
        String title = null;
        String major =  Major.values()[0].name();

        // when
        Page<Post> posts = postRepository.findPosts(pageable, title, major);

        // then
        assertThat(posts.getTotalElements()).isEqualTo(10);
        assertThat(posts.getTotalPages()).isEqualTo(1);
        assertThat(posts.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(posts.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @Transactional
    void findAllPostsByTitleTest(){
        // given
        Pageable pageable = PageRequest.of(0,10);
        String title = "title";
        String major = null;

        // when
        Page<Post> posts = postRepository.findPosts(pageable, title, major);

        // then
        assertThat(posts.getTotalElements()).isEqualTo(50);
        assertThat(posts.getTotalPages()).isEqualTo(5);
        assertThat(posts.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(posts.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @Transactional
    void findAllPostsByMajorAndTitleTest(){
        // given
        Pageable pageable = PageRequest.of(0,10);

        String title = "title0";
        String major =  Major.values()[0].name();

        // when
        Page<Post> posts = postRepository.findPosts(pageable, title, major);

        // then
        assertThat(posts.getTotalElements()).isEqualTo(10);
        assertThat(posts.getTotalPages()).isEqualTo(1);
        assertThat(posts.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(posts.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @Transactional
    void findAllPostsOrderByCreatedAtTest(){
        // given
        Pageable pageable = PageRequest.of(0,10, Sort.by(Sort.Direction.DESC, "createdAt"));

        String title = null;
        String major = null;

        // when
        Page<Post> posts = postRepository.findPosts(pageable, title, major);
        List<Post> content = posts.getContent();

        // then
        assertThat(posts.getTotalElements()).isEqualTo(50);
        assertThat(posts.getTotalPages()).isEqualTo(5);
        assertThat(posts.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(posts.getPageable().getPageSize()).isEqualTo(10);

        for(int i=0;i<content.size()-1;i++){
            LocalDateTime current = content.get(i).getCreatedAt();
            LocalDateTime next = content.get(i+1).getCreatedAt();
            assertThat(current).isAfterOrEqualTo(next);
        }
    }
}