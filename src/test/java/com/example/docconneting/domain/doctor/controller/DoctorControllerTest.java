package com.example.docconneting.domain.doctor.controller;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.doctor.dto.DoctorResponse;
import com.example.docconneting.domain.doctor.service.DoctorService;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    private DoctorService doctorService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        String majorName = "INTERNAL_MEDICINE";
        Major major = Major.of(majorName);

        user1 = new User(
                "test1@naver.com",
                "password",
                "kimdoctor",
                major,
                "https://example.com/image.jpg",
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                false,
                UserRole.DOCTOR
        );
        ReflectionTestUtils.setField(user1, "id", 1L);

        user2 = new User(
                "test2@naver.com",
                "password",
                "leedoctor",
                major,
                "https://example.com/image.jpg",
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                false,
                UserRole.DOCTOR
        );
        ReflectionTestUtils.setField(user2, "id", 2L);
    }

    @Test
    public void 의사_단건_조회() throws Exception {
        // given
        long userId = 1L;
        String username = "kimdoctor";
        String major = "INTERNAL_MEDICINE";
        String imageUrl = "https://example.com/image.jpg";
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(18, 0);

        given(doctorService.findDoctor(userId)).willReturn(new DoctorResponse(userId, username, major, imageUrl, startTime, endTime));

        // when & then
        mockMvc.perform(get("/api/v1/doctors/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(username))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.imageUrl").value(imageUrl));
    }

    @Test
    public void 의사_다건_조회() throws Exception {
        // given
        int page = 1;
        int size = 5;
        int totalElement = 2;
        int totalPages = 1;

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        List<DoctorResponse> doctors = DoctorResponse.toDoctorResponse(users);

        PageInfo pageInfo = PageInfo.builder()
                .pageNum(page)
                .pageSize(size)
                .totalElement(totalElement)
                .totalPage(totalPages)
                .build();

        PageResult<DoctorResponse> pageResult = new PageResult<>(doctors, pageInfo);

        given(doctorService.findDoctors(page, size, "", "")).willReturn(pageResult);

        // when & then
        mockMvc.perform(get("/api/v1/doctors")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("category", "")
                .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(user1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name").value(user1.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].id").value(user2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].name").value(user2.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.pageNum").value(page))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.pageSize").value(size))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElement").value(totalElement))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalPage").value(totalPages));
    }
}