package com.example.docconneting.domain.coupon.dto.response;

import com.example.docconneting.domain.coupon.entity.Coupon;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateCouponResponse {

    private final Long id;
    private final Integer availableCount;
    private final Integer quantity;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    private CreateCouponResponse(Long id, Integer availableCount, Integer quantity, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.availableCount = availableCount;
        this.quantity = quantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static CreateCouponResponse of(Long id, Integer availableCount, Integer quantity, LocalDateTime startDate, LocalDateTime endDate) {
        return new CreateCouponResponse(id, availableCount, quantity, startDate, endDate);
    }
}
