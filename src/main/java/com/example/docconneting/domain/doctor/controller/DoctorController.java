package com.example.docconneting.domain.doctor.controller;

import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.doctor.dto.DoctorResponse;
import com.example.docconneting.domain.doctor.service.DoctorService;
import com.example.docconneting.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    // 의사 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<Response<DoctorResponse>> findDoctor(@PathVariable Long id){
        DoctorResponse response = doctorService.findDoctor(id);
        return ResponseEntity.ok(Response.of(response));
    }

    // 의사 다건 조회
    @GetMapping()
    public ResponseEntity<Response<List<User>>> findDoctors(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name)
    {
        PageResult<User> pageResult = doctorService.findDoctors(page, size, category, name);
        return ResponseEntity.ok().body(Response.of(pageResult.getContent(), pageResult.getPageInfo()));
    }
}
