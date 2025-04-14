package com.example.docconneting.domain.chatting.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomCreateResponse;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomListResponse;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomSingleResponse;
import com.example.docconneting.domain.chatting.entity.ChattingRoom;
import com.example.docconneting.domain.chatting.repository.ChattingRoomRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChattingRoomService {
    private final ChattingRoomRepository chattingRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChattingRoomCreateResponse createdChattingRoom(AuthUser authUser, Long doctorId){

        if (!UserRole.PATIENT.equals(authUser.getUserRole())){

            throw new ClientException(ErrorCode.ONLY_PATIENT_CAN_CREATE_CHATTING_ROOM);

        }

        User patient = userRepository.findByPatientId(authUser.getId()).orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

        User doctor = userRepository.findByDoctorId(doctorId).orElseThrow(() -> new ClientException(ErrorCode.DOCTOR_NOT_FOUND));

        // 채팅방을 가져와서 비활성화 된 상태면 다시 활성화 시킨다
        Optional<ChattingRoom> chattingRoomOptional = chattingRoomRepository.findChattingRoomByPatientAndDoctor(patient.getId(), doctor.getId());
        if (!chattingRoomOptional.isEmpty()){

            ChattingRoom findChattingRoom = chattingRoomOptional.get();

            if (findChattingRoom.getIsActive()){

                throw new ClientException(ErrorCode.CHATTING_ROOM_ALREADY_EXIST);

            }
            else {

                findChattingRoom.setIsActive(true);

                return ChattingRoomCreateResponse.of(
                        findChattingRoom.getId(),
                        patient.getId(),
                        doctor.getId(),
                        true,
                        findChattingRoom.getCreatedAt());

            }
        }

        ChattingRoom chattingRoom = ChattingRoom.of(doctor, patient, true);

        ChattingRoom savedChattingRoom = chattingRoomRepository.save(chattingRoom);

        return ChattingRoomCreateResponse.of(
                savedChattingRoom.getId(),
                patient.getId(),
                doctor.getId(),
                false,
                savedChattingRoom.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public ChattingRoomSingleResponse findChattingRoomById(AuthUser authUser, Long chattingRoomId){

        ChattingRoom findChattingRoom = chattingRoomRepository.findChattingRoomWithPatientAndDoctor(chattingRoomId).orElseThrow(() -> new ClientException(ErrorCode.CHATTING_ROOM_NOT_FOUND));

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

        return ChattingRoomSingleResponse.of(
                findChattingRoom.getId(),
                findChattingRoom.getPatient().getId(),
                findChattingRoom.getDoctor().getId(),
                findChattingRoom.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public PageResult<ChattingRoomListResponse> findAllChattingRooms(AuthUser authUser, Pageable pageable){

        Page<ChattingRoom> chattingRooms = null;

        if (UserRole.PATIENT.equals(authUser.getUserRole())){

            userRepository.findByPatientId(authUser.getId()).orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

            chattingRooms = chattingRoomRepository.findPatientsChattingRooms(authUser.getId(), pageable);

        }
        else if (UserRole.DOCTOR.equals(authUser.getUserRole())){

            userRepository.findByDoctorId(authUser.getId()).orElseThrow(() -> new ClientException(ErrorCode.DOCTOR_NOT_FOUND));

            chattingRooms = chattingRoomRepository.findDoctorsChattingRooms(authUser.getId(), pageable);

        }

        List<ChattingRoom> content = chattingRooms.getContent();
        Pageable chattingRoomsPageable = chattingRooms.getPageable();

        List<ChattingRoomListResponse> chattingRoomListResponses = null;

        if (UserRole.PATIENT.equals(authUser.getUserRole())){

            chattingRoomListResponses = ChattingRoomListResponse.toChattingRoomListResponsesForPatient(content);

        }
        else if (UserRole.DOCTOR.equals(authUser.getUserRole())){

            chattingRoomListResponses = ChattingRoomListResponse.toChattingRoomListResponsesForDoctor(content);

        }

        PageInfo pageInfo = PageInfo.builder()
                .pageNum(chattingRoomsPageable.getPageNumber())
                .pageSize(chattingRoomsPageable.getPageSize())
                .totalElement(chattingRooms.getTotalElements())
                .totalPage(chattingRooms.getTotalPages())
                .build();

        return new PageResult<>(chattingRoomListResponses, pageInfo);

    }
}
