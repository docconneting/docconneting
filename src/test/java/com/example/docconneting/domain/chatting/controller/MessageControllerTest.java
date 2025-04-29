package com.example.docconneting.domain.chatting.controller;

import com.example.docconneting.common.config.JwtUtil;
import com.example.docconneting.common.filter.JwtFilter;
import com.example.docconneting.common.resolver.AuthUserArgumentResolver;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.projection.MessageList;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomListResponse;
import com.example.docconneting.domain.chatting.dto.response.MessageListResponse;
import com.example.docconneting.domain.chatting.entity.Message;
import com.example.docconneting.domain.chatting.service.ChattingRoomService;
import com.example.docconneting.domain.chatting.service.MessageService;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(MessageController.class)
@Import({JwtUtil.class, JwtFilter.class, AuthUserArgumentResolver.class})
class MessageControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    MessageService messageService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    JwtUtil jwtUtil;

    @Test
    @DisplayName("채팅방 메시지 목록 조회 api 테스트")
    void findAllMessagesTest() throws Exception {
        // given
        Long userId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        Long chattingRoomId = 1L;

        Pageable pageable = PageRequest.of(0, 10);

        User messageUser = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(messageUser, "id", 1L);

        List<MessageList> messages = new ArrayList<>();
        for(int i=0;i<10;i++){
            MessageList message = new FakeMessageList(null, null, null);
            messages.add(message);
        }

        List<MessageListResponse> content = MessageListResponse.toMessageListResponses(messages);

        PageInfo pageInfo = PageInfo.builder()
                .pageNum(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElement(content.size())
                .totalPage(content.size()/pageable.getPageSize())
                .build();

        PageResult<MessageListResponse> pageResult = new PageResult<>(content, pageInfo);

        String accessToken = jwtUtil.createToken(userId, userRole);

        given(messageService.findAllMessages(refEq(authUser), eq(chattingRoomId), argThat(
                p -> p.getPageNumber() == pageable.getPageNumber() && p.getPageSize() == pageable.getPageSize()
        ))).willReturn(pageResult);

        // when, then
        mockMvc.perform(get("/api/v1/chattingRooms/{chattingRoomId}/messages", chattingRoomId)
                .header("Authorization", accessToken))
                .andExpect(status().isOk());
    }

    static class FakeMessageList implements MessageList {
        private final Long userId;
        private final String contents;
        private final LocalDateTime createdAt;

        public FakeMessageList(Long userId, String contents, LocalDateTime createdAt) {
            this.userId = userId;
            this.contents = contents;
            this.createdAt = createdAt;
        }

        @Override
        public Long getUserId() {
            return userId;
        }

        @Override
        public String getContents() {
            return contents;
        }

        @Override
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }
}