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

import static com.example.docconneting.domain.order.enums.OrderType.CHAT;
import static com.example.docconneting.domain.order.enums.OrderType.POINT;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse createOrder(AuthUser authUser, OrderRequest orderRequest) {
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

        // 환자만 결제가 가능 (의사는 결제 기능 없음)
        if (!UserRole.PATIENT.equals(authUser.getUserRole())) { // 고도화할떄 preauth, secured 어노테이션 ->사용자 롤 사전에 확인
            throw new ClientException(ErrorCode.NOT_ALLOWED_TO_ORDER);
        }

        OrderProduct orderProduct = orderRequest.getOrderProduct();
        OrderType orderType = orderRequest.getOrderType();

        if (!orderProduct.getPrice().equals(orderRequest.getPrice())) {
            throw new ClientException(ErrorCode.INVALID_ORDER_PRICE);
        }

        Order order = switch (orderType) {
            case POINT -> {
                // 포인트 타입 상품을 고르지 않았을 때
                if (orderProduct.getOrderType() != POINT) {
                    throw new ClientException(ErrorCode.INVALID_ORDER_PRODUCT);
                }

                yield Order.ofPointOrder(user, orderProduct);
            }
            // 채팅 결제는 3000원으로 고정
            case CHAT -> {
                if (orderProduct.getOrderType() != CHAT) {
                    throw new ClientException(ErrorCode.INVALID_ORDER_PRODUCT);
                }

                yield Order.ofChatOrder(user, OrderProduct.CHAT_3000);
            }
            default -> throw new ClientException(ErrorCode.NOT_ALLOWED_TO_ORDER);
        };

        Order saved = orderRepository.save(order);

        return OrderResponse.of(
                saved.getId(),
                saved.getOrderType(),
                saved.getOrderStatus(),
                saved.getPaymentStatus(),
                saved.getPaymentMethod(),
                saved.getOrderProduct(),
                saved.getPrice(),
                saved.getChattingRoomId(),
                saved.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public OrderResponse findOrderById(AuthUser authUser, Long orderId) {
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ClientException(ErrorCode.ORDER_NOT_FOUND));

        // 환자이고 자신의 주문에만 접근가능
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
}
