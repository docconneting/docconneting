package com.example.docconneting.domain.post.repository;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.domain.post.entity.Post;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostRepositoryTest {
    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

//    @Test
//    void createPosts(){
//        User user = userRepository.findById(1L).get();
//        Random random = new Random(System.currentTimeMillis());
//        List<Post> posts = new ArrayList<>();
//        for(int i=0;i<1000000;i++){
//            int randomNumber = random.nextInt(5);
//            Major[] majors = Major.values();
//            Post post = new Post(user, "title"+(i+1), "contents"+(i+1), majors[randomNumber], false, false, false, LocalDateTime.now());
//            posts.add(post);
//        }
//        postRepository.saveAll(posts);
//    }
}