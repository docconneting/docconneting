package com.example.docconneting.domain.doctor.dto;

import com.example.docconneting.domain.user.entity.User;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DoctorResponse {

    private final Long id;
    private final String name;
    private final String major;
    private final String imageUrl;
    private final LocalTime startTime;
    private final LocalTime endTime;

    private DoctorResponse(Long id, String name, String major, String imageUrl, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.name = name;
        this.major = major;
        this.imageUrl = imageUrl;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static DoctorResponse of(Long id, String name, String major, String imageUrl, LocalTime startTime, LocalTime endTime) {
        return new DoctorResponse(id, name, major, imageUrl, startTime, endTime);
    }

    public static List<DoctorResponse> toDoctorResponse(List<User> users) {
        List<DoctorResponse> doctorResponseList = users.stream()
                .map(user -> new DoctorResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getMajor().name(),
                        user.getImage(),
                        user.getStartTime(),
                        user.getEndTime()
                ))
                .collect(Collectors.toList());
        return doctorResponseList;
    }
}
