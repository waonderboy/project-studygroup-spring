package com.practice.studygroup.controller;

import com.practice.studygroup.dto.StudyDto;
import com.practice.studygroup.dto.request.StudyForm;
import com.practice.studygroup.dto.security.CommonUserPrincipal;
import com.practice.studygroup.dto.security.CurrentUser;
import com.practice.studygroup.service.StudyService;
import com.practice.studygroup.validator.StudyFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final StudyFormValidator studyFormValidator;

    @InitBinder("studyForm")
    public void studyFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyFormValidator);
    }

    @GetMapping("/new-study")
    public String newStudyForm(@CurrentUser CommonUserPrincipal commonUserPrincipal, Model model) {
        model.addAttribute("account", commonUserPrincipal);
        model.addAttribute(StudyForm.of());
        return "study/form";
    }

    @PostMapping("/new-study")
    public String newStudySubmit(@CurrentUser CommonUserPrincipal commonUserPrincipal, @Valid StudyForm studyForm,
                                 Errors errors, Model model) {
        studyFormValidator.validate(studyForm, errors);

        if (errors.hasErrors()) {
            model.addAttribute("account", commonUserPrincipal);
            return "study/form";
        }

        StudyDto newStudyDto = studyService.createNewStudy(studyForm.toDto(), commonUserPrincipal.toDto());

        return "redirect:/study/" + URLEncoder.encode(newStudyDto.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/study/{path}")
    public String viewStudy(@CurrentUser CommonUserPrincipal commonUserPrincipal, @PathVariable String path, Model model) {
        StudyDto studyDto = studyService.getStudy(path);
        model.addAttribute("account", commonUserPrincipal); // for main header
        model.addAttribute("study", studyDto);
        return "study/view";
    }

}
