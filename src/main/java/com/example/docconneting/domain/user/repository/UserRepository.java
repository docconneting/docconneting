package com.example.docconneting.domain.user.repository;

import com.example.docconneting.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryQuery {

    @Query("SELECT u FROM User u " +
            "WHERE u.id = :id " +
            "AND u.userRole = 'DOCTOR' " +
            "AND u.isDeleted = FALSE ")
    Optional<User> findByDoctorId(Long id);

    Optional<User> findByEmail(String email);
}
