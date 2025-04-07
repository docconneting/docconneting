package com.example.docconneting.domain.comment.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ServerException;
import com.example.docconneting.domain.comment.dto.request.CommentRequestDto;
import com.example.docconneting.domain.comment.dto.response.CommentResponseDto;
import com.example.docconneting.domain.comment.entity.Comment;
import com.example.docconneting.domain.comment.repository.CommentRepository;
import com.example.docconneting.domain.post.entity.Post;
import com.example.docconneting.domain.post.repository.PostRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() throws Exception {
        user = new User("test@test.com", "test!123", "name");
        ReflectionTestUtils.setField(user, "id", 1L);

        post = new Post();
        Field postIdField = Post.class.getDeclaredField("id");
        ReflectionTestUtils.setField(post, "id", 1L);
    }

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CreateCommentTest {

        @Test
        @Order(1)
        void 사용자가_없으면_예외() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            CommentRequestDto request = new CommentRequestDto("comments");

            ServerException ex = assertThrows(ServerException.class, () ->
                    commentService.createComment(1L, 1L, request)
            );

            assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
        }

        @Test
        @Order(2)
        void 게시글이_없으면_예외() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(postRepository.findById(1L)).thenReturn(Optional.empty());

            CommentRequestDto request = new CommentRequestDto("comments");

            ServerException ex = assertThrows(ServerException.class, () ->
                    commentService.createComment(1L, 1L, request)
            );

            assertEquals(ErrorCode.POST_NOT_FOUND, ex.getErrorCode());
        }

        @Test
        @Order(3)
        void 정상적으로_댓글_생성() {
            CommentRequestDto request = new CommentRequestDto("comments");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(postRepository.findById(1L)).thenReturn(Optional.of(post));
            when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> {
                Comment c = inv.getArgument(0);
                Field idField = Comment.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(c, 10L);
                return c;
            });

            CommentResponseDto response = commentService.createComment(1L, 1L, request);

            assertNotNull(response);
            assertEquals("comments", response.getId());
            assertEquals(10L, response.getId());
        }
    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class UpdateCommentTest {

        private Comment comment;

        @BeforeEach
        void setUp() throws Exception {
            comment = new Comment(user, post, "comments");
            Field commentIdField = Comment.class.getDeclaredField("id");
            commentIdField.setAccessible(true);
            commentIdField.set(comment, 100L);
        }

        @Test
        @Order(1)
        void 댓글이_없으면_예외() {
            when(commentRepository.findById(100L)).thenReturn(Optional.empty());

            CommentRequestDto request = new CommentRequestDto("updateComments");

            ServerException ex = assertThrows(ServerException.class, () ->
                    commentService.updateComment(1L, 100L, request)
            );

            assertEquals(ErrorCode.COMMENT_NOT_FOUND, ex.getErrorCode());
        }

        @Test
        @Order(2)
        void 작성자가_아니면_예외() throws Exception {
            User user = new User("test@test.com", "test!123", "name");
            Field commentIdField = Comment.class.getDeclaredField("id");
            commentIdField.setAccessible(true);
            commentIdField.set(user, 99L);

            Comment anotherComment = new Comment(user, post, "anotherComments");
            Field commentIdField2 = Comment.class.getDeclaredField("id");
            commentIdField2.setAccessible(true);
            commentIdField2.set(anotherComment, 100L);

            when(commentRepository.findById(100L)).thenReturn(Optional.of(anotherComment));

            CommentRequestDto request = new CommentRequestDto("updateComments");

            ServerException ex = assertThrows(ServerException.class, () ->
                    commentService.updateComment(1L, 100L, request)
            );

            assertEquals(ErrorCode.NOT_COMMENT_OWNER, ex.getErrorCode());

        }

        @Test
        @Order(3)
        void 정상적으로_댓글_수정() {
            when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));
            when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

            CommentRequestDto request = new CommentRequestDto("updateComments");

            CommentResponseDto response = commentService.updateComment(1L, 100L, request);

            assertNotNull(response);
            assertEquals(100L, response.getId());
            assertEquals("updateComments", response.getId());
        }
    }
}