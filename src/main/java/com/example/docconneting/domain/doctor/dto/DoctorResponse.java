package com.example.docconneting.domain.doctor.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class DoctorResponse {

    private final Long id;
    private final String name;
    private final String major;
    private final String imageUrl;
    private final LocalTime startTime;
    private final LocalTime endTime;

    @Builder
    public DoctorResponse(Long id, String name, String major, String imageUrl, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.name = name;
        this.major = major;
        this.imageUrl = imageUrl;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
