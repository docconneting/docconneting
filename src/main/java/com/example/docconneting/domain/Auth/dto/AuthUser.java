package com.example.docconneting.domain.Auth.dto;

import com.example.docconneting.domain.user.enums.UserRole;
import lombok.Getter;

@Getter
public class AuthUser {
    private final Long id;
    private final UserRole userRole;

    public AuthUser(Long id, UserRole userRole) {
        this.id = id;
        this.userRole = userRole;
    }
}
