package com.example.docconneting.domain.order.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.order.entity.Order;
import com.example.docconneting.domain.order.dto.request.OrderRequest;
import com.example.docconneting.domain.order.dto.response.OrderResponse;
import com.example.docconneting.domain.order.enums.OrderProduct;
import com.example.docconneting.domain.order.enums.OrderStatus;
import com.example.docconneting.domain.order.enums.OrderType;
import com.example.docconneting.domain.order.repository.OrderRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;

    @BeforeEach
    void setUp() throws Exception {
        user = User.of(
                "test@test.com",
                "test!123",
                "name",
                0,
                false,
                UserRole.PATIENT
        );
        ReflectionTestUtils.setField(user, "id", 1L);
    }

    void givenUserExists(UserRole role) {
        ReflectionTestUtils.setField(user, "userRole", role);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    }

    private Order createTestOrder(User user, OrderType type, OrderProduct product, int price) {
        Order order = switch (type) {
            case POINT -> Order.ofPointOrder(user, product);
            case CHAT -> Order.ofChatOrder(user, product);
            default -> throw new ClientException(ErrorCode.INVALID_ORDER_TYPE);
        };
        ReflectionTestUtils.setField(order, "id", 1L);
        ReflectionTestUtils.setField(order, "orderStatus", OrderStatus.REQUESTED);
        ReflectionTestUtils.setField(order, "price", price);
        ReflectionTestUtils.setField(order, "createdAt", LocalDateTime.now());

        return order;
    }

    private OrderRequest createOrderRequest(OrderType type, OrderProduct product, int price) {
        OrderRequest request = new OrderRequest();
        ReflectionTestUtils.setField(request, "orderType", type);
        ReflectionTestUtils.setField(request, "orderProduct", product);
        ReflectionTestUtils.setField(request, "price", price);
        return request;
    }

        @Nested
        class CreateOrderTest {

            @Test
            void 사용자가_없으면_예외() {
                when(userRepository.findById(1L)).thenReturn(Optional.empty());
                OrderRequest orderRequest = createOrderRequest(OrderType.POINT, OrderProduct.POINT_5000, 5000);
                AuthUser authUser = AuthUser.of(1L, UserRole.PATIENT);

                ClientException ex = assertThrows(
                        ClientException.class,
                        () -> orderService.createOrder(authUser, orderRequest)
                );
                assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
            }

            @Test
            void 환자만_결제_가능() {
                OrderRequest orderRequest = createOrderRequest(OrderType.POINT, OrderProduct.POINT_5000, 5000);

                for (UserRole role : new UserRole[]{UserRole.DOCTOR, UserRole.ADMIN}) {
                    ReflectionTestUtils.setField(user, "userRole", role);
                    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
                    AuthUser authUser = AuthUser.of(1L, role);

                    ClientException ex = assertThrows(
                            ClientException.class,
                            () -> orderService.createOrder(authUser, orderRequest)
                    );
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NOT_ALLOWED_TO_ORDER);
                }
            }

            @Test
            void 포인트_주문_정상_처리() {
                givenUserExists(UserRole.PATIENT);
                OrderRequest orderRequest = createOrderRequest(OrderType.POINT, OrderProduct.POINT_5000, 5000);
                AuthUser authUser = AuthUser.of(1L, UserRole.PATIENT);

                Order savedOrder = createTestOrder(user, OrderType.POINT, OrderProduct.POINT_5000, 5000);
                when(orderRepository.save(any())).thenReturn(savedOrder);

                OrderResponse response = orderService.createOrder(authUser, orderRequest);

                assertThat(response).isNotNull();
                assertThat(response.getOrderType()).isEqualTo(OrderType.POINT);
                assertThat(response.getOrderProduct()).isEqualTo(OrderProduct.POINT_5000);
                assertThat(response.getPrice()).isEqualTo(5000);
            }

            @Test
            void 포인트_상품과_가격이_불일치하면_예외() {
                givenUserExists(UserRole.PATIENT);
                OrderRequest orderRequest = createOrderRequest(OrderType.POINT, OrderProduct.POINT_5000, 3000);
                AuthUser authUser = AuthUser.of(1L, UserRole.PATIENT);

                ClientException ex = assertThrows(
                        ClientException.class,
                        () -> orderService.createOrder(authUser, orderRequest)
                );

                assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER_PRICE);
            }

            @Test
            void 채팅_결제_정상_처리() {
                givenUserExists(UserRole.PATIENT);
                OrderRequest orderRequest = createOrderRequest(OrderType.CHAT, OrderProduct.CHAT_3000, 3000);
                AuthUser authUser = AuthUser.of(1L, UserRole.PATIENT);

                Order testOrder = createTestOrder(user, OrderType.CHAT, OrderProduct.CHAT_3000, 3000);
                when(orderRepository.save(any())).thenReturn(testOrder);

                OrderResponse response = orderService.createOrder(authUser, orderRequest);

                assertThat(response).isNotNull();
                assertThat(response.getOrderType()).isEqualTo(OrderType.CHAT);
                assertThat(response.getPrice()).isEqualTo(3000);
            }
        }
    }