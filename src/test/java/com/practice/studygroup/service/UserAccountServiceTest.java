package com.practice.studygroup.service;

import com.practice.studygroup.config.AppProperties;
import com.practice.studygroup.domain.UserAccount;
import com.practice.studygroup.dto.UserAccountDto;
import com.practice.studygroup.mail.EmailService;
import com.practice.studygroup.repository.UserAccountRepository;
import lombok.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DisplayName("서비스 - 회원가입")
@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @InjectMocks
    private UserAccountService sut;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private AppProperties appProperties;
    @Mock
    private TemplateEngine templateEngine;

    @DisplayName("회원가입 정보가 주어지면, 비밀번호를 인코딩해 디비에 저장 후 메일을 발송한다")
    @Test
    void UserAccountDto_SaveEncodedUserAccount_DoNothing() {
        // Given
        UserAccountDto dto = createUserAccountDto();
        UserAccount entity = dto.toEntity(passwordEncoder);
        appProperties.setHost("http://localhost:8080");
        Context context = new Context();
        given(userAccountRepository.save(entity)).willReturn(entity);
        given(userAccountRepository.findByEmail(dto.getEmail())).willReturn(Optional.of(entity));
        given(appProperties.getHost()).willReturn("http://localhost:8080");


        // When
        sut.processNewUserAccount(dto);


        // Then
        assertThat(dto.getPassword()).isNotSameAs(entity.getPassword());
        then(userAccountRepository).should().save(entity);
        then(userAccountRepository).should().findByEmail(dto.getEmail());

    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.builder()
                .email("bilnd@naver.com")
                .password("adddfas@!#")
                .nickname("blind13")
                .build();
    }

}