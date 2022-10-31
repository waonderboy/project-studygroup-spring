package com.practice.studygroup.controller;

import com.practice.studygroup.dto.response.ProfileForm;
import com.practice.studygroup.dto.security.CommonUserPrincipal;
import com.practice.studygroup.dto.security.CurrentUser;
import com.practice.studygroup.service.UserAccountService;
import com.practice.studygroup.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {
    static final String SETTING_PROFILE_VIEW = "settings/profile";
    private final UserAccountService userAccountService;

    @GetMapping("/profile")
    public String ProfileForm(@CurrentUser CommonUserPrincipal commonUserPrincipal, Model model) {
        // TODO: 예외 리팩토링 필요
        if (commonUserPrincipal == null) {
            throw new AuthenticationCredentialsNotFoundException("인증된 유저가 아닙니다.");
        }

        ProfileForm profile = userAccountService.getUserAccountProfile(commonUserPrincipal.getNickname());

        model.addAttribute("profile", profile);
        return SETTING_PROFILE_VIEW;
    }


    @PostMapping("/profile")
    public String updateProfile(@CurrentUser CommonUserPrincipal commonUserPrincipal, @Validated ProfileForm profileForm, Errors errors, Model model) {
        // TODO: 예외 리팩토링 필요
        if (commonUserPrincipal == null) {
            throw new AuthenticationCredentialsNotFoundException("인증된 유저가 아닙니다.");
        }

        if (errors.hasErrors()) {
            model.addAttribute("profile", profileForm);
            return SETTING_PROFILE_VIEW;
        }

        String userNickname = commonUserPrincipal.getNickname();
        ProfileForm updatedProfile = userAccountService.updateUserAccountProfile(userNickname, profileForm);

        model.addAttribute("profile", updatedProfile);

        return "redirect:/profile/" + userNickname;
    }
}
