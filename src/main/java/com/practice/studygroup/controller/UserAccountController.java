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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        userAccountService.loginAfterModifyInfo(signUpForm.getEmail());

        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {

        if (userAccountService.isCorrectTokenAndVerifyEmail(email, token)) {
            CommonUserPrincipal commonUserPrincipal = userAccountService.loginAfterModifyInfo(email);
            model.addAttribute("nickname", commonUserPrincipal.getNickname());
            return "account/checked-email";
        }

        model.addAttribute("error", "wrong.email");
        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser CommonUserPrincipal commonUserPrincipal, Model model) {

        if(!userAccountService.canSendConfirmEmail(commonUserPrincipal.getEmail())){
            model.addAttribute("error", "????????? ????????? 3?????? ????????? ???????????????.");
            model.addAttribute("email", commonUserPrincipal.getEmail());
            return "account/checked-email";
        }

        // ????????? ??????
        userAccountService.sendSignUpConfirmEmail(commonUserPrincipal.getEmail());

        return "redirect:/";
    }

    @GetMapping("/profile/{nickname}")
    public String profile(@PathVariable String nickname, @CurrentUser CommonUserPrincipal commonUserPrincipal, Model model) {
        ProfileForm userProfile = userAccountService.getUserAccountProfile(nickname);
        boolean isOwner = true;
        if (userProfile == null) {
            throw new IllegalArgumentException(nickname + "??? ???????????? ????????? ????????????.");
        }
        if (commonUserPrincipal == null || !nickname.equals(commonUserPrincipal.getNickname())) {
            isOwner = false;
        }
        model.addAttribute("account", userProfile);
        model.addAttribute("isOwner", isOwner);
        return "account/profile";
    }


    @GetMapping("/email-sign-in")
    public String signInByEmail() {
        return "account/email-sign-in";
    }

    @PostMapping("/email-sign-in")
    public String sendEmailLoginLink(String email, Model model, RedirectAttributes attributes) {
        if (!userAccountService.checkValidEmail(email)) {
            model.addAttribute("error", "???????????? ?????? ????????? ???????????????.");
            return "account/email-sign-in";
        }

        if (userAccountService.canSendConfirmEmail(email)) {
            model.addAttribute("error", "????????? ???????????? 1?????? ?????? ????????? ??? ????????????.");
            return "account/email-sign-in";
        }

        userAccountService.sendEmailLoginLink(email);
        attributes.addFlashAttribute("message", "??????????????? ?????? ????????????");
        return "redirect:/email-sign-in";
    }

    @GetMapping("/sign-in-by-email")
    public String signInByEmail(@RequestParam String token, @RequestParam String email, Model model) {
        if (!userAccountService.isCorrectTokenAndVerifyEmail(email, token)) {
               model.addAttribute("error", "????????? ??? ??? ????????????.");
               return "account/signed-in-by-email";
        }

        userAccountService.loginAfterModifyInfo(email);
        return "account/signed-in-by-email";
    }
}
