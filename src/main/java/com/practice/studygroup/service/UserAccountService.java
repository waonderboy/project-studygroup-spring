package com.practice.studygroup.service;

import com.practice.studygroup.domain.UserAccount;
import com.practice.studygroup.dto.UserAccountDto;
import com.practice.studygroup.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void processNewUserAccount(UserAccountDto userAccountdto) {
        UserAccount newUserAccount = userAccountRepository.save(userAccountdto.toEntity(passwordEncoder));
        newUserAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newUserAccount);
    }

    private void sendSignUpConfirmEmail(UserAccount userAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userAccount.getEmail());
        mailMessage.setSubject("스터디그룹, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + userAccount.getEmailCheckToken()
                + "&email=" + userAccount.getEmail());

        javaMailSender.send(mailMessage); //TODO : SMTP로 구현필요
    }

    /**
     * authentication Filter, Manager, Provider, Service 등 나중에 고도화 시
     * 시큐리티 빈으로 등록해서 주입후 사용
     * @param
     */
    public UserAccountDto loginAfterSignUp(String email) {
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElse(null);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userAccount.getNickname(),
                userAccount.getPassword(),
                Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(token);

        return UserAccountDto.from(userAccount);
    }

    @Transactional
    public boolean isNotCorrectTokenAndSignUp(String email, String token) {
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElse(null);

        return (userAccount != null && userAccount.isValidToken(token)) ?
                userAccount.completeSignUp() : false;
    }

}
