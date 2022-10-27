package com.practice.studygroup.controller;

import com.practice.studygroup.FormDataEncoder;
import com.practice.studygroup.config.SecurityConfig;
import com.practice.studygroup.domain.UserAccount;
import com.practice.studygroup.dto.UserAccountDto;
import com.practice.studygroup.dto.request.SignUpForm;
import com.practice.studygroup.repository.UserAccountRepository;
import com.practice.studygroup.service.UserAccountService;
import com.practice.studygroup.validator.SignUpFormValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TEST Method Name Convention
 * (given)condition_(when)behavior_(then)result
 * 준비_실행_검증 by BDD
 */
@DisplayName("View 컨트롤러 - 회원가입")
@Import({SecurityConfig.class, SignUpFormValidator.class, FormDataEncoder.class})
@WebMvcTest
class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FormDataEncoder dataEncoder;
    @MockBean
    private UserAccountService userAccountService;

    @MockBean
    private UserAccountRepository userAccountRepository;


    @DisplayName("[View][GET] - 회원가입 페이지 요청")
    @Test
    void NoArgs_RequestingSignUpFrom_SignUpForm() throws Exception {
        // Given
        // When & Then
        mockMvc.perform(
                        get("/sign-up")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @DisplayName("[View][POST] - 회원가입 성공")
    @Test
    void SignUpInfo_RequestingSignUp_SaveUserAccount() throws Exception {
        // Given
        SignUpForm normalSignUpForm = createNormalSignUpForm();
        UserAccountDto dto = normalSignUpForm.toDto();
        willDoNothing().given(userAccountService).processNewUserAccount(dto);

        // When
        mockMvc.perform(
                        post("/sign-up")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(dataEncoder.encode(normalSignUpForm))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/home"))
                .andExpect(redirectedUrl("/home"));

        // Then
        then(userAccountService).should().processNewUserAccount(dto);
    }

    @DisplayName("[View][POST] - 회원가입 검증 실패")
    @Test
    void SignUpInfo_RequestingSignUp_ReturnValidationError() throws Exception {
        // Given
        SignUpForm abnormalSignUpForm = createAbnormalSignUpForm();
        UserAccountDto dto = abnormalSignUpForm.toDto();
        willDoNothing().given(userAccountService).processNewUserAccount(abnormalSignUpForm.toDto());

        // When & // Then
        mockMvc.perform(
                        post("/sign-up")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(dataEncoder.encode(abnormalSignUpForm))
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("account/sign-up"));

        // Then

    }

    @DisplayName("[POST] -인증 메일 확인 실패")
    @Test
    void checkEmailToken_with_wrong_input() throws Exception {
        // Given
        String email = "kkkkk@naver.com";
        String token = "This Is Plain Text";
        UserAccountDto dto = UserAccountDto.builder().email(email).build();
        given(userAccountService.isNotCorrectTokenAndSignUp(email, token)).willReturn(true);
        given(userAccountService.loginAfterSignUp(email)).willReturn(dto);

        // When
        mockMvc.perform(get("/check-email-token")
                        .param("token", token)
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());

        // Then
        then(userAccountService).should().isNotCorrectTokenAndSignUp(email, token);

    }


    @DisplayName("[POST] -인증 메일 확인 성공")
    @WithMockUser
    @Test
    void checkEmailToken() throws Exception {
        // Given
        String email = "kkkkk@naver.com";
        String token = UUID.randomUUID().toString();
        String nick = "user";
        UserAccountDto dto = UserAccountDto.builder()
                .email(email)
                .nickname(nick)
                .build();
        given(userAccountService.isNotCorrectTokenAndSignUp(email, token)).willReturn(false);
        given(userAccountService.loginAfterSignUp(email)).willReturn(dto);

        // When
        mockMvc.perform(get("/check-email-token")
                        .param("token", token)
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated().withUsername(nick));

        // Then
        then(userAccountService).should().isNotCorrectTokenAndSignUp(email, token);
        then(userAccountService).should().loginAfterSignUp(email);
    }

    private SignUpForm createNormalSignUpForm() {
        return SignUpForm.of("normal@naver.com", "kimkim", "asdfa!@#");
    }

    private SignUpForm createAbnormalSignUpForm() {
        return SignUpForm.of("normal@naver.com", "ki", "asdfa!@#");
    }

}