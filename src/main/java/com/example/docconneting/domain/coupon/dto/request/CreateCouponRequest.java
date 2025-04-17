package com.example.docconneting.domain.coupon.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class CreateCouponRequest {

    @NotNull(message = "quantity는 필수입니다.")
    @Positive(message = "1 이상이어야 합니다.")
    private Integer quantity;

    private CreateCouponRequest(Integer quantity) {
        this.quantity = quantity;
    }

    // 테스트 코드에서 필요
    public static CreateCouponRequest of(@NotNull Integer quantity) {
        return new CreateCouponRequest(quantity);
    }
}
