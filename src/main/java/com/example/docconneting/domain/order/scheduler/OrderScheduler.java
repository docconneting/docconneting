package com.example.docconneting.domain.order.scheduler;

import com.example.docconneting.domain.order.entity.Order;
import com.example.docconneting.domain.order.enums.OrderStatus;
import com.example.docconneting.domain.order.repository.OrderRepository;
import com.example.docconneting.domain.payment.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderRepository orderRepository;

    // 1분마다 실행
    @Scheduled(fixedRate = 60 * 1000)
    @Transactional
    public void expireUnpaidOrders() {
        LocalDateTime expirationThreshold = LocalDateTime.now().minusMinutes(5); // 5분 지나면 만료

        List<Order> expiredCandidates = orderRepository.findAllByStatusBeforeTime(
                OrderStatus.REQUESTED,
                PaymentStatus.REQUESTED,
                expirationThreshold
        );

        for (Order order : expiredCandidates) {
            order.updateOrderStatus(OrderStatus.EXPIRED);
            order.updatePaymentStatus(PaymentStatus.FAILED);
            log.info("만료된 주문 처리 완료 : " + order.getId());
        }
    }
}
