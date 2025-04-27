package com.example.docconneting.domain.order.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class OrderServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        user = User.of(
                "test@test.com",
                "test123!",
                "환자",
                0,
                false,
                UserRole.PATIENT
        );
        ReflectionTestUtils.setField(user, "id", 1L);

        authUser = AuthUser.of(1L, UserRole.PATIENT);
    }

    void givenUserExists(UserRole role) {
        ReflectionTestUtils.setField(user, "userRole", role);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    }

    private Order createOrder(OrderRequest orderRequest, String merchantUid, AuthUser authUser) {
        Order order = switch (orderRequest.getOrderType()) {
            case POINT -> Order.ofPointOrder(user, orderRequest.getOrderProduct(), "merchant-uid");
            case CHAT -> Order.ofChatOrder(user, orderRequest.getOrderProduct(), 50L, "merchant-uid");
        };
        ReflectionTestUtils.setField(order, "id", 1L);
        ReflectionTestUtils.setField(order, "user", user);
        ReflectionTestUtils.setField(order, "price", orderRequest.getPrice());
        ReflectionTestUtils.setField(order, "orderStatus", OrderStatus.COMPLETED);
        ReflectionTestUtils.setField(order, "merchantUid", merchantUid);
        ReflectionTestUtils.setField(order, "approvedAt", LocalDateTime.now());

        return order;
    }

        private OrderRequest createOrderRequest (OrderType type, OrderProduct product,int price, Long doctorId){
            OrderRequest request = new OrderRequest();
            ReflectionTestUtils.setField(request, "orderType", type);
            ReflectionTestUtils.setField(request, "orderProduct", product);
            ReflectionTestUtils.setField(request, "price", price);
            ReflectionTestUtils.setField(request, "doctorId", doctorId);
            return request;
        }

        @Nested
        class CreateOrderTest {

            @Test
            @DisplayName("사용자가 없으면 예외")
            void 사용자가_없으면_예외() {
                when(userRepository.findById(1L)).thenReturn(Optional.empty());
                OrderRequest orderRequest = createOrderRequest(OrderType.POINT, OrderProduct.POINT_5000, 5000, null);
                AuthUser authUser = AuthUser.of(1L, UserRole.PATIENT);

                ClientException ex = assertThrows(ClientException.class, () ->
                        orderService.createOrder(orderRequest, "merchant-uid", authUser));
                assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
            }

            @Test
            @DisplayName("가격이 다르면 예외")
            void 가격이_다르면_예외() {
                givenUserExists(UserRole.PATIENT);

                OrderRequest orderRequest = createOrderRequest(OrderType.POINT, OrderProduct.POINT_5000, 1000, null);
                AuthUser authUser = AuthUser.of(1L, UserRole.PATIENT);

                ClientException ex = assertThrows(ClientException.class, () ->
                        orderService.createOrder(orderRequest, "merchant-uid", authUser));

                assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER_PRICE);
            }

            @Test
            @DisplayName("상품과 타입이 다르면 예외")
            void 상품과_타입이_다르면_예외() {
                givenUserExists(UserRole.PATIENT);

                OrderRequest orderRequest = createOrderRequest(OrderType.CHAT, OrderProduct.POINT_5000, 5000, null);
                ReflectionTestUtils.setField(orderRequest, "doctorId", 50L);
                AuthUser authUser = AuthUser.of(1L, UserRole.PATIENT);

                ClientException ex = assertThrows(ClientException.class, () ->
                        orderService.createOrder(orderRequest, "merchant-uid", authUser));
                assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER_PRODUCT);
            }
        }

    @Nested
    class FinfByMerchantUidOrThrowTest {

        @Test
        @DisplayName("merchantUid 존재하면 주문 리턴")
            void merchantUid_존재하면_주문_리턴() {
            OrderRequest orderRequest = createOrderRequest(OrderType.POINT, OrderProduct.POINT_5000, 5000, null);
            AuthUser authUser = AuthUser.of(1L, UserRole.PATIENT);
            Order order = createOrder(orderRequest, "merchant-uid", authUser);

            when(orderRepository.findByMerchantUid("merchant-uid")).thenReturn(Optional.of(order));

            Order result = orderService.findByMerchantUidOrThrow("merchant-uid");

            assertThat(result).isEqualTo(order);
        }

        @Test
        @DisplayName("merchantUid가 존재하지 않으면 예외")
            void merchantUid가_존재하지_않으면_예외() {
            when(orderRepository.findByMerchantUid("invalid-uid")).thenReturn(Optional.empty());

            ClientException ex = assertThrows(ClientException.class, () ->
                    orderService.findByMerchantUidOrThrow("invalid-uid"));

            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ORDER_NOT_FOUND);
        }
    }

    @Nested
    class FindOrderByIdTest {

        @Test
        @DisplayName("본인 주문이면 성공")
            void 본인_주문이면_성공() {
            givenUserExists(UserRole.PATIENT);

            OrderRequest orderRequest = createOrderRequest(OrderType.POINT, OrderProduct.POINT_5000, 5000, null);
            AuthUser authUser = AuthUser.of(1L, UserRole.PATIENT);
            Order order = createOrder(orderRequest, "merchant-uid", authUser);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            OrderResponse result = orderService.findOrderById(authUser, 1L);
            assertThat(result.getId()).isEqualTo(order.getId());
            assertThat(result.getOrderProduct()).isEqualTo(order.getOrderProduct());
        }

        @Test
        @DisplayName("환자가 아니면 예외 발생")
            void 환자가_아니면_예외_발생() {
            givenUserExists(UserRole.DOCTOR);

            OrderRequest orderRequest = createOrderRequest(OrderType.POINT, OrderProduct.POINT_5000, 5000, null);
            AuthUser authUser = AuthUser.of(1L, UserRole.DOCTOR);
            Order order = createOrder(orderRequest, "merchant-uid", authUser);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            ClientException ex = assertThrows(ClientException.class, () ->
                    orderService.findOrderById(authUser, 1L));

            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_ORDER_ACCESS);
        }

        @Test
        @DisplayName("타인의 주문이면 에외")
            void 타인의_주문이면_예외() {
            givenUserExists(UserRole.PATIENT);

            User anotherUser = User.of("aaa@aaa.com","pwd", "환자2", 0, false, UserRole.PATIENT);
            ReflectionTestUtils.setField(anotherUser, "id", 2L);

            OrderRequest orderRequest = createOrderRequest(OrderType.POINT, OrderProduct.POINT_1000, 1000, null);
            AuthUser authUser = AuthUser.of(1L, UserRole.PATIENT);
            Order order = switch (orderRequest.getOrderType()) {
                case POINT -> Order.ofPointOrder(anotherUser, orderRequest.getOrderProduct(), "merchant-uid");
                case CHAT -> createOrder(orderRequest, "merchant-uid", authUser);
            };
            ReflectionTestUtils.setField(order, "id", 1L);
            ReflectionTestUtils.setField(order, "user", anotherUser);
            ReflectionTestUtils.setField(order, "price", orderRequest.getPrice());
            ReflectionTestUtils.setField(order, "orderStatus", OrderStatus.COMPLETED);
            ReflectionTestUtils.setField(order, "merchantUid", "merchant-uid");
            ReflectionTestUtils.setField(order, "approvedAt", LocalDateTime.now());

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            ClientException ex = assertThrows(ClientException.class, () ->
                    orderService.findOrderById(authUser, 1L));

            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_ORDER_ACCESS);
        }
    }

    @Nested
    class FindOrdersTest {

        @Test
        @DisplayName("환자라면 주문 목록 조회 성공")
        void 환자라면_주문목록_조회성공() {
            givenUserExists(UserRole.PATIENT);

            OrderRequest orderRequest = createOrderRequest(OrderType.POINT, OrderProduct.POINT_5000, 5000, null);
            AuthUser authUser = AuthUser.of(1L, UserRole.PATIENT);
            Order order = createOrder(orderRequest, "merchant-uid", authUser);

            List<Order> orderList = List.of(order);
            Page<Order> orderPage = new PageImpl<>(orderList);

            when(orderRepository.findAllByUser(user, Pageable.ofSize(10))).thenReturn(orderPage);

            PageResult<OrderResponse> result = orderService.findOrders(authUser, Pageable.ofSize(10));

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getPageInfo().getTotalElement()).isEqualTo(1);
            assertThat(result.getContent().get(0).getOrderProduct()).isEqualTo(OrderProduct.POINT_5000);
        }

        @Test
        @DisplayName("환자가 아니라면 예외 발생")
            void 환자가_아니라면_예외_발생() {
            givenUserExists(UserRole.DOCTOR);

            AuthUser authUser = AuthUser.of(1L, UserRole.DOCTOR);

            ClientException ex = assertThrows(ClientException.class, () ->
                    orderService.findOrders(authUser, Pageable.ofSize(10)));

            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NOT_ALLOWED_TO_ORDER);
        }
    }

    @Nested
    class AssignChattingRoomTest {

        @Test
        @DisplayName("채팅 주문이면서 완료 상태면 성공")
            void 채팅_주문이면서_완료_상태면_성공() {
            OrderRequest orderRequest = createOrderRequest(OrderType.CHAT, OrderProduct.CHAT_3000, 3000, 50L);
            AuthUser authUser = AuthUser.of(1L, UserRole.PATIENT);
            Order order = createOrder(orderRequest, "merchant-uid", authUser);

            ReflectionTestUtils.setField(order, "orderStatus", OrderStatus.COMPLETED);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            orderService.assignChattingRoomId(1L, 30L);

            assertThat(order.getChattingRoomId()).isEqualTo(30L);
        }

        @Test
        @DisplayName("채팅 주문이 아니거나 완료 상태가 아니면 예외")
            void 채팅_주문이_아니거나_완료_상태가_아니면_예외() {
            OrderRequest orderRequest = createOrderRequest(OrderType.POINT, OrderProduct.POINT_5000, 5000, null);
            AuthUser authUser = AuthUser.of(1L, UserRole.PATIENT);
            Order order = createOrder(orderRequest, "merchant-uid", authUser);

            ReflectionTestUtils.setField(order, "orderStatus", OrderStatus.EXPIRED);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            ClientException ex = assertThrows(ClientException.class, () ->
                    orderService.assignChattingRoomId(1L, 30L));

            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ORDER_NOT_FOUND);
        }
    }
}
