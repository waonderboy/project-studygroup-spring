package com.practice.studygroup.controller;

import com.practice.studygroup.FormDataEncoder;
import com.practice.studygroup.config.SecurityConfig;
import com.practice.studygroup.dto.UserAccountDto;
import com.practice.studygroup.dto.request.SignUpForm;
import com.practice.studygroup.dto.security.CommonUserPrincipal;
import com.practice.studygroup.repository.UserAccountRepository;
import com.practice.studygroup.service.UserAccountService;
import com.practice.studygroup.validator.SignUpFormValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 초기데이터가 생기면 @WithUserDetails를 사용해서 단위테스트로 전환
 */
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("홈 컨트롤러 - 로그인 로그아웃")
class HomeControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserAccountService userAccountService;
    @Autowired UserAccountRepository accountRepository;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("kimkim11");
        signUpForm.setEmail("kimkim11@email.com");
        signUpForm.setPassword("12345678");
        userAccountService.processNewUserAccount(signUpForm.toDto());
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @DisplayName("[POST] - 이메일로 로그인 성공")
    @Test
    void login_with_email() throws Exception {
        mockMvc.perform(post("/sign-in")
                        .param("username", "kimkim11@email.com")
                        .param("password", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("kimkim11"));
    }

    @DisplayName("[POST] - 닉네임으로 로그인 성공")
    @Test
    void login_with_nickname() throws Exception {
        mockMvc.perform(post("/sign-in")
                        .param("username", "kimkim11")
                        .param("password", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("kimkim11"));
    }

    @DisplayName("[POST] - 로그인 실패")
    @Test
    void login_fail() throws Exception {
        mockMvc.perform(post("/sign-in")
                        .param("username", "111111")
                        .param("password", "000000000")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sign-in?error"))
                .andExpect(unauthenticated());
    }

    @WithMockUser
    @DisplayName("[POST] - 로그아웃 성공")
    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }


}