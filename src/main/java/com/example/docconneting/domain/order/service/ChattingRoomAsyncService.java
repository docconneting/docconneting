package com.example.docconneting.domain.order.service;

import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomCreateResponse;
import com.example.docconneting.domain.chatting.repository.ChattingRoomRepository;
import com.example.docconneting.domain.chatting.service.ChattingRoomService;
import com.example.docconneting.domain.order.entity.Order;
import com.example.docconneting.domain.order.repository.OrderRepository;
import com.example.docconneting.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChattingRoomAsyncService {

    private final ChattingRoomRepository chattingRoomRepository;
    private final OrderRepository orderRepository;
    private final ChattingRoomService chattingRoomService;

    @Async("chattingRoomExecutor")
    @Transactional
    public void createChattingRoom(Order order) {
        try {
            Long doctorId = order.getDoctorId();
            Long userId = order.getUser().getId();

            AuthUser authUser = AuthUser.of(userId, UserRole.PATIENT);

            ChattingRoomCreateResponse response = chattingRoomService.createdChattingRoom(authUser, doctorId);

            // 채팅방 id 주문에 연결
            order.assignChattingRoomId(response.getId());
            orderRepository.save(order);

            log.info("채팅방 비동기 생성 완료 - orderId={}, chattingRoomId={}", order.getId(), response.getId());

        } catch (Exception e) {
            log.error("채팅방 생성 실패 - orderId={}", order.getId(), e);
        }
    }
}
