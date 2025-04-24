package com.example.docconneting.domain.order.service;

import com.example.docconneting.domain.chatting.entity.ChattingRoom;
import com.example.docconneting.domain.chatting.repository.ChattingRoomRepository;
import com.example.docconneting.domain.order.entity.Order;
import com.example.docconneting.domain.order.enums.OrderProduct;
import com.example.docconneting.domain.order.repository.OrderRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ChattingRoomAsyncServiceTest {

    @Autowired
    private ChattingRoomAsyncService chattingRoomAsyncService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ChattingRoomRepository chattingRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("비동기 채팅방 생성 성공 테스트")
    void 비동기_채팅방_생성_성공_테스트() throws InterruptedException {
        User patient = createUser("test@patient.com", "환자", UserRole.PATIENT);
        User doctor = createUser("test@doctor.com", "의사", UserRole.DOCTOR);
        userRepository.saveAll(java.util.List.of(patient, doctor));

        String uniqueMerchantUid = "merchant_" + UUID.randomUUID();
        Order order = Order.ofChatOrder(patient, OrderProduct.CHAT_3000, doctor.getId(), uniqueMerchantUid);
        orderRepository.save(order);

        // when
        chattingRoomAsyncService.createChattingRoom(order);

        // then
        Awaitility.await()
                .atMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Order updatedOrder = orderRepository.findById(order.getId())
                            .orElseThrow(() -> new IllegalStateException("Order not found after async creation"));
                    assertThat(updatedOrder.getChattingRoomId())
                            .as("채팅방 ID가 생성되어야 합니다.")
                            .isNotNull();
                });

        Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
        ChattingRoom room = chattingRoomRepository.findChattingRoomByPatientAndDoctor(patient.getId(), doctor.getId()).orElse(null);

        assertThat(room)
                .as("채팅방이 생성되어야 합니다.")
                .isNotNull();
        assertThat(room.getIsActive())
                .as("채팅방은 활성 상태여야 합니다.")
                .isTrue();
    }

    private User createUser(String email, String username, UserRole role) {
        return User.of(email, "password123!", username, 0, false, role);
    }
}