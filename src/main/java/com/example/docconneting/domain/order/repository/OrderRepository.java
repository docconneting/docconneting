package com.example.docconneting.domain.order.repository;

import com.example.docconneting.domain.order.entity.Order;
import com.example.docconneting.domain.order.enums.OrderStatus;
import com.example.docconneting.domain.payment.enums.PaymentStatus;
import com.example.docconneting.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUser(User user, Pageable pageable);
    Optional<Order> findByMerchantUid(String merchantUid);

    @Query("""
    SELECT o FROM Order o
    WHERE o.orderStatus = :orderStatus
      AND o.paymentStatus = :paymentStatus
      AND o.createdAt <= :threshold
""")
    List<Order> findAllByStatusBeforeTime(@Param("orderStatus") OrderStatus orderStatus,
                                          @Param("paymentStatus") PaymentStatus paymentStatus,
                                          @Param("threshold") LocalDateTime threshold);
}