package com.example.docconneting.domain.order.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.order.dto.request.OrderRequest;
import com.example.docconneting.domain.order.dto.response.OrderResponse;
import com.example.docconneting.domain.order.entity.Order;
import com.example.docconneting.domain.order.enums.OrderProduct;
import com.example.docconneting.domain.order.enums.OrderStatus;
import com.example.docconneting.domain.order.enums.OrderType;
import com.example.docconneting.domain.order.repository.OrderRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    // 결제 완료 후 주문 생성
    public Order createOrder(OrderRequest orderRequest, String merchantUid, AuthUser authUser) {
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(()-> new ClientException(ErrorCode.USER_NOT_FOUND));
        OrderProduct orderProduct = orderRequest.getOrderProduct();
        OrderType orderType = orderRequest.getOrderType();

        if (!orderProduct.getPrice().equals(orderRequest.getPrice())) {
            throw new ClientException(ErrorCode.INVALID_ORDER_PRICE);
        }
        if (!orderProduct.getOrderType().equals(orderType)) {
            throw new ClientException(ErrorCode.INVALID_ORDER_PRODUCT);
        }
        return switch (orderType) {
            case POINT -> orderRepository.save(Order.ofPointOrder(user,orderProduct, merchantUid));
            case CHAT -> orderRepository.save(Order.ofChatOrder(user,orderProduct, orderRequest.getDoctorId(), merchantUid));
        };
    }

    @Transactional(readOnly = true)
    public OrderResponse findOrderById(AuthUser authUser, Long orderId) {
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ClientException(ErrorCode.ORDER_NOT_FOUND));

        if (!UserRole.PATIENT.equals(authUser.getUserRole()) ||
                !authUser.getId().equals(order.getUser().getId())) {
            throw new ClientException(ErrorCode.FORBIDDEN_ORDER_ACCESS);
        }

        return OrderResponse.of(
                order.getId(),
                order.getOrderType(),
                order.getOrderStatus(),
                order.getPaymentStatus(),
                order.getPaymentMethod(),
                order.getOrderProduct(),
                order.getPrice(),
                order.getChattingRoomId(),
                order.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public PageResult<OrderResponse> findOrders(AuthUser authUser, Pageable pageable) {
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

        if (!UserRole.PATIENT.equals(authUser.getUserRole())) {
            throw new ClientException(ErrorCode.NOT_ALLOWED_TO_ORDER);
        }

        Page<Order> orders = orderRepository.findAllByUser(user, pageable);
        List<OrderResponse> orderResponses = OrderResponse.toOrderResponseList(orders.getContent());

        PageInfo pageInfo = PageInfo.builder()
                .pageNum(orders.getNumber())
                .pageSize(orders.getSize())
                .totalElement(orders.getTotalElements())
                .totalPage(orders.getTotalPages())
                .build();

        return new PageResult<>(orderResponses, pageInfo);
    }

    @Transactional
    public void assignChattingRoomId(Long orderId, Long chattingRoomId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ClientException(ErrorCode.ORDER_NOT_FOUND));

        if (!OrderType.CHAT.equals(order.getOrderType()) || !OrderStatus.COMPLETED.equals(order.getOrderStatus())) {
            throw new ClientException(ErrorCode.ORDER_NOT_FOUND);
        }

        order.assignChattingRoomId(chattingRoomId);
    }
}
