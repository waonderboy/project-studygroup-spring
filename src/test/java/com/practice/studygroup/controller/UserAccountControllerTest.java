package com.practice.studygroup.controller;

import com.practice.studygroup.FormDataEncoder;
import com.practice.studygroup.config.SecurityConfig;
import com.practice.studygroup.config.TestSecurityConfig;
import com.practice.studygroup.domain.UserAccount;
import com.practice.studygroup.dto.UserAccountDto;
import com.practice.studygroup.dto.request.SignUpForm;
import com.practice.studygroup.dto.security.CommonUserPrincipal;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
@DisplayName("유저 컨트롤러 - 회원가입")
@Import({TestSecurityConfig.class, SignUpFormValidator.class, FormDataEncoder.class})
@WebMvcTest(UserAccountController.class)
class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FormDataEncoder dataEncoder;
    @MockBean
    private UserAccountService userAccountService;


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
                .andExpect(view().name("redirect:/"))
                .andExpect(redirectedUrl("/"));

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

    @DisplayName("[GET] -인증 메일 확인 실패")
    @Test
    void TokenAndEmail_RequestingCheckTokenAboutEmail_ReturnError() throws Exception {
        // Given
        String email = "kkkkk@naver.com";
        String token = "This Is Plain Text";
        UserAccountDto dto = UserAccountDto.builder().email(email).build();
        given(userAccountService.isCorrectTokenAndSignUp(email, token)).willReturn(false);
        given(userAccountService.loginAfterModifyInfo(email)).willReturn(CommonUserPrincipal.from(dto));

        // When
        mockMvc.perform(get("/check-email-token")
                        .param("token", token)
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/check-email"))
                .andExpect(unauthenticated());

        // Then
        then(userAccountService).should().isCorrectTokenAndSignUp(email, token);

    }


    @DisplayName("[GET] -인증 메일 확인 성공")
    @WithMockUser
    @Test
    void TokenAndEmail_RequestingCheckTokenAboutEmail_ConfirmEmail() throws Exception {
        // Given
        String email = "kkkkk@naver.com";
        String token = UUID.randomUUID().toString();
        String nick = "user";
        UserAccountDto dto = UserAccountDto.builder()
                .email(email)
                .nickname(nick)
                .build();
        given(userAccountService.isCorrectTokenAndSignUp(email, token)).willReturn(true);
        given(userAccountService.loginAfterModifyInfo(email)).willReturn(CommonUserPrincipal.from(dto));

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
        then(userAccountService).should().isCorrectTokenAndSignUp(email, token);
        then(userAccountService).should().loginAfterModifyInfo(email);
    }

    @DisplayName("[GET] - 인증 메일 재발송 성공")
    @WithMockUser
    @Test
    void Nothing_RequestingResendEmailToken_ResendEmailToken() throws Exception {
        // Given
        CommonUserPrincipal principal = createCommonUserPrincipal();

        given(userAccountService.canSendConfirmEmail(principal.getEmail())).willReturn(true);
        willDoNothing().given(userAccountService).sendSignUpConfirmEmail(principal.getEmail());

        // When
        mockMvc.perform(get("/resend-confirm-email")
                        .with(user(principal)))
                .andExpect(status().is3xxRedirection());

        // Then
        then(userAccountService).should().canSendConfirmEmail(principal.getEmail());
        then(userAccountService).should().sendSignUpConfirmEmail(principal.getEmail());
    }

    @DisplayName("[GET] - 인증 메일 재발송 실패")
    @WithMockUser
    @Test
    void Nothing_RequestingResendEmailToken_DoNothing() throws Exception {
        // Given
        CommonUserPrincipal principal = createCommonUserPrincipal();

        given(userAccountService.canSendConfirmEmail(principal.getEmail())).willReturn(false);
        willDoNothing().given(userAccountService).sendSignUpConfirmEmail(principal.getEmail());

        // When
        mockMvc.perform(get("/resend-confirm-email")
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("email"));

        // Then
        then(userAccountService).should().canSendConfirmEmail(principal.getEmail());
    }


    private static CommonUserPrincipal createCommonUserPrincipal() {
        String email = "kkkkk@naver.com";
        String token = UUID.randomUUID().toString();
        String nick = "user";
        CommonUserPrincipal principal = CommonUserPrincipal.builder().nickname(nick).email(email).build();
        return principal;
    }


    private SignUpForm createNormalSignUpForm() {
        return new SignUpForm("normal@naver.com", "kimkim", "asdfa!@#");
    }

    private SignUpForm createAbnormalSignUpForm() {
        return new SignUpForm("normal@naver.com", "ki", "asdfa!@#");
    }

}