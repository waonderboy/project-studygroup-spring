package com.practice.studygroup.controller;

import com.practice.studygroup.dto.security.CommonUserPrincipal;
import com.practice.studygroup.dto.security.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@CurrentUser CommonUserPrincipal commonUserPrincipal, Model model) {
        model.addAttribute("account", commonUserPrincipal);
        return "index";
    }
}
