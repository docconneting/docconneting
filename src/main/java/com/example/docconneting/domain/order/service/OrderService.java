package com.example.docconneting.domain.order.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.exception.object.ServerException;
import com.example.docconneting.domain.order.dto.request.OrderRequest;
import com.example.docconneting.domain.order.dto.response.OrderResponse;
import com.example.docconneting.domain.order.entity.Order;
import com.example.docconneting.domain.order.enums.OrderProduct;
import com.example.docconneting.domain.order.repository.OrderRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest orderRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerException(ErrorCode.USER_NOT_FOUND));

        if (!UserRole.PATIENT.equals(user.getUserRole())) {
            throw new ServerException(ErrorCode.NOT_ALLOWED_TO_ORDER);
        }

        Order order = switch (orderRequest.getOrderType()) {
            case POINT -> {
                OrderProduct orderProduct = orderRequest.getOrderProduct();
                if (orderProduct == null) {
                    throw new ClientException(ErrorCode.ORDER_PRODUCT_NOT_FOUND);
                }

                if (!orderProduct.getPrice().equals(orderRequest.getPrice())) {
                    throw new ClientException(ErrorCode.INVALID_ORDER_PRICE);
                }

                yield Order.ofPointOrder(user, orderProduct);
            }
            case CHAT -> Order.ofChatOrder(user, OrderProduct.CHAT_3000);
            default -> throw new ClientException(ErrorCode.NOT_ALLOWED_TO_ORDER);
        };

        Order saved = orderRepository.save(order);

        return OrderResponse.of(
                saved.getId(),
                saved.getOrderType(),
                saved.getOrderStatus(),
                saved.getOrderProduct(),
                saved.getPrice(),
                saved.getChattingRoomId(),
                saved.getCreatedAt()
            );
        }
    }
