package com.example.docconneting.domain.doctor.service;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.doctor.dto.DoctorResponse;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private DoctorService doctorService;

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
        long userId = 1L;
        String email = "test@naver.com";
        String password = "password";
        String username = "kimdoctor";
        String majorName = "INTERNAL_MEDICINE";
        Major major = Major.of(majorName);
        String image = "https://example.com/image.jpg";
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(18, 0);
        boolean isDeleted = false;
        UserRole userRole = UserRole.DOCTOR;
        User user = new User(email, password, username, major, image, startTime, endTime, isDeleted, userRole);
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findByDoctorId(userId)).willReturn(Optional.of(user));

        // when
        DoctorResponse response = doctorService.findDoctor(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getName()).isEqualTo(username);
        assertThat(response.getMajor()).isEqualTo(majorName);
    }

    @Test
    public void 의사_목록을_페이징을_적용하여_조회할_수_있다() {
        // given
        int page = 1;
        int size = 5;
        String category = "INTERNAL_MEDICINE";
        String name = "kimdoctor";

        List<User> users = List.of(mock(User.class), mock(User.class), mock(User.class), mock(User.class), mock(User.class));
        Page<User> pageResult = new PageImpl<>(users, PageRequest.of(page - 1, 5), 5);
        Mockito.when(userRepository.findDoctors(PageRequest.of(page - 1, size), category, name)).thenReturn(pageResult);

        // when
        PageResult<User> result = doctorService.findDoctors(page, size, category, name);

        // then
        assertEquals(5, result.getContent().size());
        assertEquals(5, result.getPageInfo().getTotalElement());
        assertEquals(page, result.getPageInfo().getPageNum());
        assertEquals(size, result.getPageInfo().getPageSize());
    }
}