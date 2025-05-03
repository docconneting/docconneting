package com.example.docconneting.domain.coupon.repository;

import com.example.docconneting.domain.coupon.entity.FailedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FailedMessageRepository extends JpaRepository<FailedMessage, Long> {
    List<FailedMessage> findByResolvedFalse();
}
