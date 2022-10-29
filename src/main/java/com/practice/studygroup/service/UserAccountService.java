package com.practice.studygroup.service;

import com.practice.studygroup.security.service.CommonUserPrincipal;
import com.practice.studygroup.domain.UserAccount;
import com.practice.studygroup.dto.UserAccountDto;
import com.practice.studygroup.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserAccountService {
    
    private final UserAccountRepository userAccountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void processNewUserAccount(UserAccountDto userAccountdto) {
        UserAccount newUserAccount = userAccountRepository.save(userAccountdto.toEntity(passwordEncoder));
        sendSignUpConfirmEmail(newUserAccount.getEmail());
    }

    @Transactional
    public void sendSignUpConfirmEmail(String email) {
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElse(null);
        userAccount.generateEmailCheckToken();

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
     */
    public CommonUserPrincipal loginAfterSignUp(String email) {
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElse(null);
        CommonUserPrincipal commonUserPrincipal = CommonUserPrincipal.from(UserAccountDto.from(userAccount));

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                commonUserPrincipal,
                userAccount.getPassword(),
                Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(token);

        return commonUserPrincipal;
    }

    @Transactional
    public boolean isCorrectTokenAndSignUp(String email, String token) {
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElse(null);

        return (userAccount != null && userAccount.isValidToken(token)) ?
                userAccount.completeSignUp() : false;
    }

    public boolean canSendConfirmEmail(CommonUserPrincipal commonUserPrincipal) {
        return userAccountRepository.findByEmail(commonUserPrincipal.getEmail()).orElse(null).canResendToken();
    }

}
