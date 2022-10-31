package com.practice.studygroup.controller;


import com.practice.studygroup.dto.request.SignUpForm;
import com.practice.studygroup.dto.response.ProfileForm;
import com.practice.studygroup.dto.security.CommonUserPrincipal;
import com.practice.studygroup.dto.security.CurrentUser;
import com.practice.studygroup.service.UserAccountService;
import com.practice.studygroup.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final SignUpFormValidator signUpFormValidator;

    @InitBinder("signUpForm")
    public void init(WebDataBinder dataBinder) {
        dataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("signUpForm", SignUpForm.of());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(@Valid SignUpForm signUpForm, Errors errors) {

        if (errors.hasErrors()) {
            return "account/sign-up";
        }

        userAccountService.processNewUserAccount(signUpForm.toDto());
        userAccountService.loginAfterSignUp(signUpForm.getEmail());

        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {

        if (userAccountService.isCorrectTokenAndSignUp(email, token)) {
            CommonUserPrincipal commonUserPrincipal = userAccountService.loginAfterSignUp(email);
            model.addAttribute("nickname", commonUserPrincipal.getNickname());
            return "account/checked-email";
        }

        model.addAttribute("error", "wrong.email");
        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser CommonUserPrincipal commonUserPrincipal, Model model) {

        if(!userAccountService.canSendConfirmEmail(commonUserPrincipal)){
            model.addAttribute("error", "이메일 인증은 3분에 한번만 가능합니다.");
            model.addAttribute("email", commonUserPrincipal.getEmail());
            return "account/checked-email";
        }

        // 재발급 가능
        userAccountService.sendSignUpConfirmEmail(commonUserPrincipal.getEmail());

        return "redirect:/";
    }

    @GetMapping("/profile/{nickname}")
    public String profile(@PathVariable String nickname, @CurrentUser CommonUserPrincipal commonUserPrincipal, Model model) {
        ProfileForm userProfile = userAccountService.getUserAccountProfile(nickname);
        boolean isOwner = true;
        if (userProfile == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 유저가 없습니다.");
        }
        if (commonUserPrincipal == null || !nickname.equals(commonUserPrincipal.getNickname())) {
            isOwner = false;
        }
        model.addAttribute("account", userProfile);
        model.addAttribute("isOwner", isOwner);
        return "account/profile";
    }

}
