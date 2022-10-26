package com.practice.studygroup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeRequests()
                .mvcMatchers("/sign-up", "/home")
                .permitAll()
                .mvcMatchers(HttpMethod.POST, "/sign-up")
                .permitAll()
                .and()
                .build();
    }
}
