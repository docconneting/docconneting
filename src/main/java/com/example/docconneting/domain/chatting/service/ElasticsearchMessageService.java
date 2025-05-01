package com.example.docconneting.domain.chatting.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.projection.MessageList;
import com.example.docconneting.domain.chatting.dto.response.ElasticsearchMessageListResponse;
import com.example.docconneting.domain.chatting.dto.response.MessageListResponse;
import com.example.docconneting.domain.chatting.entity.ChattingRoom;
import com.example.docconneting.domain.chatting.entity.ElasticsearchMessage;
import com.example.docconneting.domain.chatting.repository.ChattingRoomRepository;
import com.example.docconneting.domain.chatting.repository.ElasticsearchMessageRepository;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticsearchMessageService {

    private final ElasticsearchMessageRepository elasticsearchMessageRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final UserRepository userRepository;

    public void saveMessage(Long userId, Long chattingRoomId, String contents){

        ElasticsearchMessage elasticsearchMessage = ElasticsearchMessage.of(userId, chattingRoomId, contents);

        elasticsearchMessageRepository.save(elasticsearchMessage);
    }

    public PageResult<ElasticsearchMessageListResponse> findMessagesByKeyword(AuthUser authUser, Long chattingRoomId, String keyword, Pageable pageable){

        ChattingRoom findChattingRoom = chattingRoomRepository.findById(chattingRoomId).orElseThrow(() -> new ClientException(ErrorCode.CHATTING_ROOM_NOT_FOUND));

        if (UserRole.PATIENT.equals(authUser.getUserRole())){

            userRepository.findByPatientId(authUser.getId()).orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

            if (authUser.getId() != findChattingRoom.getPatient().getId()){
                throw new ClientException(ErrorCode.FORBIDDEN_CHATTING_ROOM_ACCESS);
            }

        }
        else if (UserRole.DOCTOR.equals(authUser.getUserRole())){

            userRepository.findByDoctorId(authUser.getId()).orElseThrow(() -> new ClientException(ErrorCode.DOCTOR_NOT_FOUND));

            if (authUser.getId() != findChattingRoom.getDoctor().getId()){
                throw new ClientException(ErrorCode.FORBIDDEN_CHATTING_ROOM_ACCESS);
            }

        }

        if (!findChattingRoom.getIsActive()){

            throw new ClientException(ErrorCode.INACTIVE_CHATTING_ROOM);

        }

        Page<ElasticsearchMessage> messages = elasticsearchMessageRepository.findMessagesByKeyword(chattingRoomId, keyword, pageable);

        List<ElasticsearchMessage> content = messages.getContent();

        Pageable messagesPageable = messages.getPageable();

        List<ElasticsearchMessageListResponse> messageListResponses = ElasticsearchMessageListResponse.toMessageListResponses(content);

        PageInfo pageInfo = PageInfo.builder()
                .pageNum(messagesPageable.getPageNumber())
                .pageSize(messagesPageable.getPageSize())
                .totalElement(messages.getTotalElements())
                .totalPage(messages.getTotalPages())
                .build();

        return new PageResult<>(messageListResponses, pageInfo);
    }
}
