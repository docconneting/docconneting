package com.example.docconneting.domain.order.repository;

import com.example.docconneting.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
