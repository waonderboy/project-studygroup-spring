package com.practice.studygroup.security;

import com.practice.studygroup.security.service.CommonUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;


@RequiredArgsConstructor
public class CommonAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {



        String email = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        CommonUserPrincipal commonUserPrincipal = (CommonUserPrincipal) userDetailsService.loadUserByUsername(email);


        if(!passwordEncoder.matches(password, commonUserPrincipal.getPassword())){
            throw new IllegalStateException("패스워드 불일치");
        }

        return new UsernamePasswordAuthenticationToken(
                commonUserPrincipal.getUsername(),
                commonUserPrincipal.getPassword(),
                commonUserPrincipal.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
