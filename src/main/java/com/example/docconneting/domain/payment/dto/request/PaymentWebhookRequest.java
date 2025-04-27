package com.example.docconneting.domain.payment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentWebhookRequest {

    @JsonProperty("imp_uid")
    private String impUid;

    @JsonProperty("merchant_uid")
    private String merchantUid;

    @JsonProperty("status")
    private String paymentStatus;

    @JsonProperty("pay_method")
    private String payMethod;

    private PaymentWebhookRequest(String impUid, String merchantUid, String paymentStatus, String payMethod) {
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.paymentStatus = paymentStatus;
        this.payMethod = payMethod;
    }

    public static PaymentWebhookRequest of(String impUid, String merchantUid, String status, String payMethod) {
        return new PaymentWebhookRequest(impUid, merchantUid, status, payMethod);
    }
}
