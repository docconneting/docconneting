package com.example.docconneting.domain.Auth.entity;

import com.example.docconneting.domain.user.enums.UserRole;
import lombok.Getter;

@Getter
public class AuthUser {
    private final Long id;
    private final UserRole userRole;

    private AuthUser(Long id, UserRole userRole) {
        this.id = id;
        this.userRole = userRole;
    }

    public static AuthUser of(Long id, UserRole userRole)
    {
        return new AuthUser(id, userRole);
    }
}
