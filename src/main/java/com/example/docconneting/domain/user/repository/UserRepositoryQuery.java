package com.example.docconneting.domain.user.repository;

import com.example.docconneting.domain.user.entity.User;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

public interface UserRepositoryQuery {
    Page<User> findDoctors(Pageable pageable, String category, String name);

}
