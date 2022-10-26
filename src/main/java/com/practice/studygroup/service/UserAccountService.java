package com.practice.studygroup.service;

import com.practice.studygroup.domain.UserAccount;
import com.practice.studygroup.dto.UserAccountDto;
import com.practice.studygroup.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final JavaMailSender javaMailSender;
    
    public void processNewUserAccount(UserAccountDto userAccountdto) {
        UserAccount newUserAccount = userAccountRepository.save(userAccountdto.toEntity());
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

}
