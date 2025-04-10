package com.example.docconneting.domain.coupon.entity;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ServerException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Coupon {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer availableCount;

    private Integer quantity;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private Coupon(Integer availableCount, Integer quantity, LocalDateTime startDate, LocalDateTime endDate) {
        this.availableCount = availableCount;
        this.quantity = quantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Coupon of(Integer availableCount, Integer quantity, LocalDateTime startDate, LocalDateTime endDate) {
        return new Coupon(availableCount, quantity, startDate, endDate);
    }

    public void decreaseQuantity() {
        if (quantity <= 0) {
            throw new ServerException(ErrorCode.COUPON_OUT_OF_STOCK);
        }
        this.quantity--;
    }
}
