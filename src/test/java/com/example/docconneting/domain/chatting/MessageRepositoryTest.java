package com.example.docconneting.domain.chatting;

import com.example.docconneting.common.config.PasswordEncoder;
import com.example.docconneting.common.enums.Major;
import com.example.docconneting.domain.chatting.entity.ChattingRoom;
import com.example.docconneting.domain.chatting.entity.ElasticsearchMessage;
import com.example.docconneting.domain.chatting.entity.Message;
import com.example.docconneting.domain.chatting.repository.ChattingRoomRepository;
import com.example.docconneting.domain.chatting.repository.ElasticsearchMessageRepository;
import com.example.docconneting.domain.chatting.repository.MessageRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class MessageRepositoryTest {

    @Autowired
    ChattingRoomRepository chattingRoomRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ElasticsearchMessageRepository elasticsearchMessageRepository;

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

    @Test
    void saveMessages(){

        User patient = userRepository.save(User.of("patientEmail1", passwordEncoder.encode("patientPassword1"), "patient1", 0, false, UserRole.PATIENT));
        User doctor = userRepository.save(User.of("doctorEmail1", passwordEncoder.encode("doctorPassword1"), "doctor1", Major.DERMATOLOGY, "image", LocalTime.now(), LocalTime.now(), false, UserRole.DOCTOR));

        ChattingRoom chattingRoom = chattingRoomRepository.save(ChattingRoom.of(doctor, patient, true));

        List<String> wordPool = new ArrayList<>(List.of(
                "건강", "스트레스", "운동", "피부", "감기", "약", "병원", "치료", "음식", "수면",
                "피곤", "머리", "복통", "기침", "두통", "발열", "피부염", "소화불량", "통증", "상담"
        ));

        Random random = new Random();
        List<Message> messages = new ArrayList<>();
        List<ElasticsearchMessage> elasticsearchMessages = new ArrayList<>();

        for (int i = 0; i < 50000; i++) {
            int numberOfWords = 3; // 5 ~ 10개 단어 선택
            Collections.shuffle(wordPool); // 리스트를 랜덤 섞기
            String content = String.join(" ", wordPool.subList(0, numberOfWords));

            Message message = Message.of(patient, chattingRoom, content);
            ElasticsearchMessage elasticsearchMessage = ElasticsearchMessage.of(patient.getId(), chattingRoom.getId(), content);
            messages.add(message);
            elasticsearchMessages.add(elasticsearchMessage);
        }

        messageRepository.saveAll(messages);
        elasticsearchMessageRepository.saveAll(elasticsearchMessages);
    }

}

