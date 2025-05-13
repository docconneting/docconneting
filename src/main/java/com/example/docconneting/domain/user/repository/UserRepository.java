package com.example.docconneting.domain.user.repository;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryQuery {

    @Query("SELECT u FROM User u " +
            "WHERE u.id = :id " +
            "AND u.userRole = 'DOCTOR' " +
            "AND u.isDeleted = FALSE ")
    Optional<User> findByDoctorId(Long id);

    @Query("SELECT u FROM User u " +
            "WHERE u.fcmToken = :id ")
    Optional<User> findByFcmToken(String fcmToken);

    Optional<User> findUserByIdAndUserRole(Long id, UserRole userRole);

    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u " +
            "WHERE u.id = :userId " +
            "AND u.fcmToken IS NOT NULL")
    Long existsByFcmToken(Long userId);

    @Query("SELECT u FROM User u " +
            "WHERE u.major = :major " +
            "AND u.isDeleted = FALSE " +
            "AND u.isAlarmEnabled = TRUE")
    List<User> findByMajor(Major major);

    @Query("SELECT u FROM User u" +
            " WHERE u.id = :id " +
            "AND u.userRole = 'PATIENT' " +
            "AND u.isDeleted = FALSE")
    Optional<User> findByPatientId(Long id);

}
