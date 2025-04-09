package com.example.docconneting.domain.chatting.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.ChattingRoomCreateResponse;
import com.example.docconneting.domain.chatting.dto.ChattingRoomSingleResponse;
import com.example.docconneting.domain.chatting.entity.ChattingRoom;
import com.example.docconneting.domain.chatting.repository.ChattingRoomRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChattingRoomService {
    private final ChattingRoomRepository chattingRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChattingRoomCreateResponse createdChattingRoom(AuthUser authUser, Long doctorId){

        if (authUser.getUserRole() != UserRole.PATIENT){
            throw new ClientException(ErrorCode.ONLY_PATIENT_CAN_CREATE_CHATTING_ROOM);
        }

        // 유저가 db에 존재하는지, 존재하면 탈퇴여부 체크하는 로직을 만들어야 되나?
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

    @Transactional
    public ChattingRoomSingleResponse findChattingRoomById(AuthUser authUser, Long chattingRoomId){

        if (authUser.getUserRole() == UserRole.PATIENT){
            userRepository.findByPatientId(authUser.getId()).orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));
        }
        else if (authUser.getUserRole() == UserRole.DOCTOR){
            userRepository.findByDoctorId(authUser.getId()).orElseThrow(() -> new ClientException(ErrorCode.DOCTOR_NOT_FOUND));
        }

        ChattingRoom findChattingRoom = chattingRoomRepository.findChattingRoomWithPatientAndDoctor(chattingRoomId).orElseThrow(() -> new ClientException(ErrorCode.CHATTING_ROOM_NOT_FOUND));

        if(!findChattingRoom.getIsActive()){
            throw new ClientException(ErrorCode.INACTIVE_CHATTING_ROOM);
        }

        return ChattingRoomSingleResponse.of(
                findChattingRoom.getId(),
                findChattingRoom.getPatient().getId(),
                findChattingRoom.getDoctor().getId(),
                findChattingRoom.getCreatedAt());
    }
}
