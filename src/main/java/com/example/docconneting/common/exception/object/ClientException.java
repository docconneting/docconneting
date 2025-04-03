package com.example.docconneting.common.exception.object;

import com.example.docconneting.common.exception.constant.ErrorCode;
import lombok.Getter;

@Getter
public class ClientException extends RuntimeException {
    private ErrorCode errorCode;

    public ClientException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}