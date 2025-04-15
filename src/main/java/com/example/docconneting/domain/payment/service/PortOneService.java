package com.example.docconneting.domain.payment.service;

import com.example.docconneting.common.config.PortOneProperties;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.Payment;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public class PortOneService {

    private final IamportClient iamportClient;

    public PortOneService(PortOneProperties properties) {
        this.iamportClient = new IamportClient(properties.getKey(), properties.getSecret());
    }

    public Payment getPayment(String impUid) throws IamportResponseException, IOException {
        return iamportClient.paymentByImpUid(impUid).getResponse();
    }
}


