package com.example.docconneting.domain.point.dto.response;

import lombok.Getter;

@Getter
public class PointResponse {

    private final Integer point;

    private PointResponse(Integer point) {
        this.point = point;
    }

    public static PointResponse of(Integer point) {
        return new PointResponse(point);
    }
}
