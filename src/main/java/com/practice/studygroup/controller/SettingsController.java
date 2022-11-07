package com.practice.studygroup.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.studygroup.domain.Tag;
import com.practice.studygroup.domain.Zone;
import com.practice.studygroup.dto.TagDto;
import com.practice.studygroup.dto.ZoneDto;
import com.practice.studygroup.dto.request.*;
import com.practice.studygroup.dto.response.ProfileForm;
import com.practice.studygroup.dto.security.CommonUserPrincipal;
import com.practice.studygroup.dto.security.CurrentUser;
import com.practice.studygroup.service.TagService;
import com.practice.studygroup.service.UserAccountService;
import com.practice.studygroup.service.ZoneService;
import com.practice.studygroup.validator.NicknameFormValidator;
import com.practice.studygroup.validator.PasswordFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {
    static final String SETTING_PROFILE_VIEW = "settings/profile";
    private final UserAccountService userAccountService;
    private final TagService tagService;
    private final ZoneService zoneService;
    private final ObjectMapper objectMapper;
    private final PasswordFormValidator passwordFormValidator;
    private final NicknameFormValidator nicknameFormValidator;

    @InitBinder("passwordForm")
    public void setPasswordFormValidator(WebDataBinder dataBinder) {
        dataBinder.addValidators(passwordFormValidator);
    }
    @InitBinder("nicknameForm")
    public void setNicknameFormValidator(WebDataBinder dataBinder) {
        dataBinder.addValidators(nicknameFormValidator);
    }

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

    @GetMapping("/password")
    public String updatePasswordForm(@CurrentUser CommonUserPrincipal commonUserPrincipal, Model model) {

        model.addAttribute("account", commonUserPrincipal);
        model.addAttribute("passwordForm", PasswordForm.of());

        return "settings/password";
    }


    @PostMapping("/password")
    public String updatePassword(@CurrentUser CommonUserPrincipal commonUserPrincipal,
                                 @Validated PasswordForm passwordForm,
                                 Errors errors,
                                 Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("account", commonUserPrincipal);
            return "settings/password";
        }

        String nickname = commonUserPrincipal.getNickname();
        userAccountService.updatePassword(nickname, passwordForm.getNewPasswordConfirm());

        return "redirect:/profile/" + nickname;
    }

    @GetMapping("/notifications")
    public String updateNotificationForm(@CurrentUser CommonUserPrincipal commonUserPrincipal, Model model) {

        model.addAttribute("account", commonUserPrincipal);
        model.addAttribute("notificationForm", NotificationForm.of());

        return "settings/notifications";
    }

    @PostMapping("/notifications")
    public String updateNotification(@CurrentUser CommonUserPrincipal commonUserPrincipal,
                                     @Validated NotificationForm notificationForm,
                                     Errors errors,
                                     Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("account", commonUserPrincipal);
            return "settings/notifications";
        }
        String nickname = commonUserPrincipal.getNickname();

        userAccountService.updateNotification(nickname, notificationForm);

        return "redirect:/profile/" + nickname;
    }

    @GetMapping("/tags")
    public String updateTagForm(@CurrentUser CommonUserPrincipal commonUserPrincipal, Model model) throws JsonProcessingException {

        model.addAttribute(commonUserPrincipal);
        Set<Tag> tags = userAccountService.getTags(commonUserPrincipal.getNickname());
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toSet()));

        List<String> allTags = tagService.getAllTags();
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
        return "settings/tags";
    }

    @PostMapping("/tags/add")
    public ResponseEntity addTag(@CurrentUser CommonUserPrincipal commonUserPrincipal, @RequestBody TagForm tagForm) {
        TagDto tagDto = tagService.findOrCreateNew(tagForm.getTagTitle());
        userAccountService.addTag(commonUserPrincipal.getNickname(), tagDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentUser CommonUserPrincipal commonUserPrincipal, @RequestBody TagForm tagForm) {

        if (!userAccountService.removeTag(commonUserPrincipal.getNickname(), tagForm.getTagTitle())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/zones")
    public String updateZoneForm(@CurrentUser CommonUserPrincipal commonUserPrincipal, Model model) throws JsonProcessingException {

        model.addAttribute(commonUserPrincipal);
        Set<Zone> zones = userAccountService.getZones(commonUserPrincipal.getNickname());
        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

        List<String> allZones = zoneService.getAllZones();
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
        return "settings/zones";
    }

    @PostMapping("/zones/add")
    public ResponseEntity addZone(@CurrentUser CommonUserPrincipal commonUserPrincipal, @RequestBody ZoneForm zoneForm) {
        ZoneDto zoneDto = zoneService.searchZone(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zoneDto == null) {
            return ResponseEntity.badRequest().build();
        }
        userAccountService.addZone(commonUserPrincipal.getNickname(), zoneDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentUser CommonUserPrincipal commonUserPrincipal, @RequestBody ZoneForm zoneForm) {

        if (!userAccountService.removeZone(commonUserPrincipal.getNickname(), zoneForm.getCityName(), zoneForm.getProvinceName())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/account")
    public String updateNicknameForm(@CurrentUser CommonUserPrincipal commonUserPrincipal, Model model) {

        model.addAttribute("account", commonUserPrincipal);
        model.addAttribute("nicknameForm", NicknameForm.of());

        return "settings/account";
    }
    @PostMapping("/account")
    public String updateNickname(@CurrentUser CommonUserPrincipal commonUserPrincipal,
                                     @Validated NicknameForm nicknameForm,
                                     Errors errors,
                                     Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("account", commonUserPrincipal);
            return "settings/account";
        }
        String nickname = commonUserPrincipal.getNickname();

        userAccountService.updateNickname(nickname, nicknameForm);

        return "redirect:/profile/" + nicknameForm.getNewNickname();
    }
}
