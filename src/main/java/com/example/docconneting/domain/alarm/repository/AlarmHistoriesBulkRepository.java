package com.example.docconneting.domain.alarm.repository;

import com.example.docconneting.domain.alarm.entity.AlarmHistories;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AlarmHistoriesBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    public int[][] batchUpdate(final List<AlarmHistories> alarmHistoriesList) {
        int [][] insertCount = jdbcTemplate.batchUpdate(
                "INSERT INTO alarm_histories(content, to_id, alarm_type, created_at)" + "VALUES (?, ?, ?, ?)",
                alarmHistoriesList,
                100,
                (PreparedStatement ps, AlarmHistories alarmHistories) -> {
                    ps.setString(1, alarmHistories.getContent());
                    ps.setString(2, String.valueOf(alarmHistories.getToId()));
                    ps.setString(3, String.valueOf(alarmHistories.getAlarmType()));
                    ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                });
        return insertCount;
    }

}
