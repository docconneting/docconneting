package com.example.docconneting.domain.alarm.repository;

import com.example.docconneting.domain.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
