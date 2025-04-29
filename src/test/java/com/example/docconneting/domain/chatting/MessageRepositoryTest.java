package com.example.docconneting.domain.chatting;

import com.example.docconneting.common.config.PasswordEncoder;
import com.example.docconneting.common.enums.Major;
import com.example.docconneting.domain.chatting.entity.ChattingRoom;
import com.example.docconneting.domain.chatting.repository.ChattingRoomRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class MessageRepositoryTest {

    @Autowired
    ChattingRoomRepository chattingRoomRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void save(){
        List<User> patients = new ArrayList<>();
        List<User> doctors = new ArrayList<>();
        for(int i=0;i<200;i++){
            if(i<100){
                User patient = userRepository.save(User.of("patientEmail"+(i+1), passwordEncoder.encode("patientPassword"+(i+1)), "patient"+(i+1), 0, false, UserRole.PATIENT));
                patients.add(patient);
            }
            else{
                User doctor = userRepository.save(User.of("doctorEmail"+(i+1), passwordEncoder.encode("doctorPassword"+(i+1)), "doctor"+(i+1), Major.DERMATOLOGY, "image"+(i+1), LocalTime.now(), LocalTime.now(), false, UserRole.DOCTOR));
                doctors.add(doctor);
            }
        }

        List<ChattingRoom> chattingRooms = new ArrayList<>();
        for(int i=0;i<100;i++){
            ChattingRoom chattingRoom = chattingRoomRepository.save(ChattingRoom.of(doctors.get(i), patients.get(i), true));
            chattingRooms.add(chattingRoom);
        }

    }
}

