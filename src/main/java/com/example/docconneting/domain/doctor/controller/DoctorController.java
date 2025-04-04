package com.example.docconneting.domain.doctor.controller;

import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.doctor.dto.DoctorResponse;
import com.example.docconneting.domain.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/{id}")
    public ResponseEntity<Response<DoctorResponse>> findDoctor(@PathVariable Long id){
        DoctorResponse response = doctorService.findDoctor(id);
        return ResponseEntity.ok(Response.of(response));
    }
}
