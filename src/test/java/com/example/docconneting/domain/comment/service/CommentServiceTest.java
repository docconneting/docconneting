package com.example.docconneting.domain.comment.service;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ServerException;
import com.example.docconneting.domain.comment.dto.request.CommentRequest;
import com.example.docconneting.domain.comment.dto.response.CommentResponse;
import com.example.docconneting.domain.comment.entity.Comment;
import com.example.docconneting.domain.comment.repository.CommentRepository;
import com.example.docconneting.domain.post.entity.Post;
import com.example.docconneting.domain.post.repository.PostRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
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
        user = User.of(
                "test@test.com",
                "test!123",
                "name",
                Major.INTERNAL_MEDICINE,   // 전공
                null,                      // image
                null,                      // startTime
                null,                      // endTime
                false,                     // isDeleted
                UserRole.DOCTOR            // 역할
        );

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

            CommentRequest request = new CommentRequest("comments");

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

            CommentRequest request = new CommentRequest("comments");

            ServerException ex = assertThrows(ServerException.class, () ->
                    commentService.createComment(1L, 1L, request)
            );

            assertEquals(ErrorCode.POST_NOT_FOUND, ex.getErrorCode());
        }

        @Test
        @Order(3)
        void 정상적으로_댓글_생성() {
            CommentRequest request = new CommentRequest("comments");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(postRepository.findById(1L)).thenReturn(Optional.of(post));
            when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> {
                Comment c = inv.getArgument(0);
                Field idField = Comment.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(c, 10L);
                return c;
            });

            CommentResponse response = commentService.createComment(1L, 1L, request);

            assertNotNull(response);
            assertEquals("comments", response.getContents());
            assertEquals(10L, response.getId());
        }

        @Test
        @Order(4)
        void 의사가_아니면_댓글_작성_불가() {
            ReflectionTestUtils.setField(user, "userRole", UserRole.PATIENT);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            CommentRequest request = new CommentRequest("comments");

            ServerException ex = assertThrows(ServerException.class, () ->
                    commentService.createComment(1L, 1L, request)
            );

            assertEquals(ErrorCode.NOT_ALLOWED_TO_COMMENT, ex.getErrorCode());
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

            CommentRequest request = new CommentRequest("updateComments");

            ServerException ex = assertThrows(ServerException.class, () ->
                    commentService.updateComment(1L, 100L, request)
            );

            assertEquals(ErrorCode.COMMENT_NOT_FOUND, ex.getErrorCode());
        }

        @Test
        @Order(2)
        void 작성자가_아니면_예외() throws Exception {
            // 현재 로그인한 사용자
            Long currentUserId = 1L;

            // 댓글의 작성자(다른 사람)
            User anotherUser = User.of(
                    "another@test.com",
                    "test!123",
                    "name",
                    Major.INTERNAL_MEDICINE,   // 전공
                    null,                      // image
                    null,                      // startTime
                    null,                      // endTime
                    false,                     // isDeleted
                    UserRole.DOCTOR            // 역할
            );

            // 다른 유저의 id(id = 99L)
            Field anotherUserIdField = User.class.getDeclaredField("id");
            anotherUserIdField.setAccessible(true);
            anotherUserIdField.set(user, 99L);

            Comment anotherComment = new Comment(anotherUser, post, "anotherComments");
            Field commentIdField = Comment.class.getDeclaredField("id");
            commentIdField.setAccessible(true);
            commentIdField.set(comment, 100L);

            when(commentRepository.findById(100L)).thenReturn(Optional.of(anotherComment));

            CommentRequest request = new CommentRequest("updateComments");

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

            CommentRequest request = new CommentRequest("updateComments");

            CommentResponse response = commentService.updateComment(1L, 100L, request);

            assertNotNull(response);
            assertEquals(100L, response.getId());
            assertEquals("updateComments", response.getContents());
        }
    }
}