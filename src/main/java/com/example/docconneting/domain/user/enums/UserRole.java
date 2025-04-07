package com.example.docconneting.domain.user.enums;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;

public enum UserRole {
    PATIENT,
    DOCTOR,
    ADMIN;

    public static UserRole of(String name) {
        for (UserRole role: values()) {
            if (role.name().equals(name)) {
                return role;
            }
        }
        throw new ClientException(ErrorCode.USERROLE_NOT_FOUND);
    }
}
