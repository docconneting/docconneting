package com.example.docconneting.domain.doctor.service;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.doctor.dto.DoctorResponse;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private DoctorService doctorService;

    private String image;
    private String majorName;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        image = "https://example.com/image.jpg";
        majorName = "INTERNAL_MEDICINE";
        Major major = Major.of(majorName);

        user1 = User.of(
                "test1@naver.com",
                "password",
                "kimdoctor",
                major,
                image,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                false,
                UserRole.DOCTOR
        );
        ReflectionTestUtils.setField(user1, "id", 1L);

        user2 = User.of(
                "test2@naver.com",
                "password",
                "leedoctor",
                major,
                image,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                false,
                UserRole.DOCTOR
        );
        ReflectionTestUtils.setField(user2, "id", 2L);
    }

    @Test
    public void 존재하지_않는_Doctor_조회시_ClientException을_던진다() {
        // given
        long userId = 1L;
        given(userRepository.findByDoctorId(userId)).willReturn(Optional.empty());

        // when & then
        ClientException exception = assertThrows(ClientException.class, () -> doctorService.findDoctor(userId));
        assertEquals("존재하지 않는 의사입니다.", exception.getErrorCode().getMessage());
    }

    @Test
    public void 의사를_ID로_조회할_수_있다() {
        // given
        given(userRepository.findByDoctorId(1L)).willReturn(Optional.of(user1));

        // when
        DoctorResponse response = doctorService.findDoctor(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("kimdoctor");
        assertThat(response.getImageUrl()).isEqualTo(image);
    }

    @Test
    public void 의사_목록을_페이징을_적용하여_조회할_수_있다() {
        // given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page,size);
        String category = "INTERNAL_MEDICINE";
        String name = "doctor";

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        Page<User> pageResult = new PageImpl<>(users, pageable, 2);
        Mockito.when(userRepository.findDoctors(pageable, category, name)).thenReturn(pageResult);

        // when
        PageResult<DoctorResponse> result = doctorService.findDoctors(pageable, category, name);

        // then
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getPageInfo().getTotalElement());
        assertEquals(page, result.getPageInfo().getPageNum());
        assertEquals(size, result.getPageInfo().getPageSize());
    }
}