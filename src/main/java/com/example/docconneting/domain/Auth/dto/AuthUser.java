package com.example.docconneting.domain.Auth.dto;

import lombok.Getter;

@Getter
public class AuthUser {
    private final Long id;

    public AuthUser(Long id) {
        this.id = id;
    }
}
