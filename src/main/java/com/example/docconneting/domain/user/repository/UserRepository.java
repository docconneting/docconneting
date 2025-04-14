package com.example.docconneting.domain.user.repository;

import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryQuery {

    @Query("SELECT u FROM User u " +
            "WHERE u.id = :id " +
            "AND u.userRole = 'DOCTOR' " +
            "AND u.isDeleted = FALSE ")
    Optional<User> findByDoctorId(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // 비관적 락
    @Query("SELECT u FROM User u WHERE u.id = :userId AND u.userRole = :userRole")
    Optional<User> findUserByIdAndUserRoleWithPessimisticLock(Long userId, UserRole userRole);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u" +
            " WHERE u.id = :id " +
            "AND u.userRole = 'PATIENT' " +
            "AND u.isDeleted = FALSE")
    Optional<User> findByPatientId(Long id);
}
