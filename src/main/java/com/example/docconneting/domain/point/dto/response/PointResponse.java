package com.example.docconneting.domain.point.dto.response;

import lombok.Getter;

@Getter
public class PointResponse {

    private final Integer point;

    public PointResponse(Integer point) {
        this.point = point;
    }
}
