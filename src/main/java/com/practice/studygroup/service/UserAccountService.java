package com.practice.studygroup.service;

import com.practice.studygroup.config.AppProperties;
import com.practice.studygroup.domain.*;
import com.practice.studygroup.dto.TagDto;
import com.practice.studygroup.dto.ZoneDto;
import com.practice.studygroup.dto.request.NicknameForm;
import com.practice.studygroup.dto.request.NotificationForm;
import com.practice.studygroup.dto.request.ZoneForm;
import com.practice.studygroup.dto.response.ProfileForm;
import com.practice.studygroup.dto.security.CommonUserPrincipal;
import com.practice.studygroup.dto.UserAccountDto;
import com.practice.studygroup.mail.EmailMessage;
import com.practice.studygroup.mail.EmailService;
import com.practice.studygroup.repository.TagRepository;
import com.practice.studygroup.repository.UserAccountRepository;
import com.practice.studygroup.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    @Transactional
    public void processNewUserAccount(UserAccountDto userAccountdto) {
        UserAccount newUserAccount = userAccountRepository.save(userAccountdto.toEntity(passwordEncoder));
        sendSignUpConfirmEmail(newUserAccount.getEmail());
    }

    @Transactional // 내부메서드 호출이므로 Transcational을 달아줘야함
    public void sendSignUpConfirmEmail(String email) {
        sendUrlAndTokenToEmail(email, "/check-email-token?token=");
    }

    @Transactional // 내부메서드 호출이므로 Transcational을 달아줘야함
    public void sendEmailLoginLink(String email) {
        sendUrlAndTokenToEmail(email, "/sign-in-by-email?token=");
    }

    @Transactional
    public void sendUrlAndTokenToEmail(String email, String url) {
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElse(null);
        userAccount.generateEmailCheckToken();

        Context context = new Context();
        context.setVariable("link", url + userAccount.getEmailCheckToken()
                + "&email=" + userAccount.getEmail());
        context.setVariable("nickname", userAccount.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "스터디그룹 서비스를 사용하려면 아래 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(userAccount.getEmail())
                .subject("스터디그룹, 회원 가입 인증")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    public CommonUserPrincipal loginAfterModifyInfo(String email) {
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
    public boolean isCorrectTokenAndVerifyEmail(String email, String token) {
        UserAccount userAccount = userAccountRepository.findByEmail(email).orElse(null);

        return (userAccount != null && userAccount.isValidToken(token)) ?
                userAccount.completeSignUp() : false;
    }

    @Transactional
    public ProfileForm updateUserAccountProfile(String nickname,ProfileForm profileForm) {
        UserAccount userAccount = userAccountRepository.findByNickname(nickname);
        userAccount.changeProfile(
                profileForm.getBio(),
                profileForm.getUrl(),
                profileForm.getOccupation(),
                profileForm.getLocation(),
                profileForm.getProfileImage()
        );

        return ProfileForm.from(userAccount);
    }

    @Transactional
    public void updateNotification(String nickname, NotificationForm notificationForm) {
        UserAccount userAccount = userAccountRepository.findByNickname(nickname);
        userAccount.changeNotification(
                notificationForm.isStudyCreatedByEmail(),
                notificationForm.isStudyCreatedByWeb(),
                notificationForm.isStudyEnrollmentResultByEmail(),
                notificationForm.isStudyEnrollmentResultByWeb(),
                notificationForm.isStudyUpdateResultByEmail(),
                notificationForm.isStudyUpdateResultByWeb());
    }

    @Transactional
    public void updatePassword(String nickname, String password) {
        UserAccount userAccount = userAccountRepository.findByNickname(nickname);
        userAccount.changePassword(passwordEncoder.encode(password));
    }

    @Transactional
    public void updateNickname(String nickname, NicknameForm nicknameForm) {
        UserAccount userAccount = userAccountRepository.findByNickname(nickname);
        userAccount.changeNickname(nicknameForm.getNewNickname());
        loginAfterModifyInfo(userAccount.getEmail());
    }

    public ProfileForm getUserAccountProfile(String nickname) {
        UserAccount userAccount = userAccountRepository.findByNickname(nickname);
        return userAccount != null ? ProfileForm.from(userAccount) : null;
    }

    public boolean checkValidEmail(String email) {
        return userAccountRepository.existsByEmail(email);
    }

    public boolean canSendConfirmEmail(String email) {
        return userAccountRepository.findByEmail(email).orElse(null).canResendToken();
    }


    public Set<Tag> getTags(String nickname) {
        return userAccountRepository.findByNickname(nickname)
                .getTags()
                .stream()
                .map(e -> e.getTag())
                .collect(Collectors.toSet());
    }

    @Transactional
    public void addTag(String nickname, TagDto tagDto) {
        UserAccount userAccount = userAccountRepository.findByNickname(nickname);
        Tag tag = tagRepository.findByTitle(tagDto.getTagTitle()).get();
        UserAccountTag userAccountTag = UserAccountTag.AddTagToUserAccount(userAccount, tag);

        userAccount.setTag(userAccountTag);
    }

    @Transactional
    public boolean removeTag(String nickname, String tagTitle) {
        UserAccount userAccount = userAccountRepository.findByNickname(nickname);
        Optional<UserAccountTag> userAccountTag = userAccount.getTags()
                .stream()
                .filter(tag -> tag
                        .getTag()
                        .getTitle()
                        .equals(tagTitle))
                .findFirst();

        userAccountTag.ifPresent(tag -> userAccount.getTags().remove(tag));

        return userAccountTag.isPresent();
    }

    public Set<Zone> getZones(String nickname) {
        return userAccountRepository.findByNickname(nickname)
                .getZones()
                .stream()
                .map(e -> e.getZone())
                .collect(Collectors.toSet());

    }

    @Transactional
    public void addZone(String nickname, ZoneDto zoneDto) {
        UserAccount userAccount = userAccountRepository.findByNickname(nickname);
        Zone zone = zoneRepository.findByCityAndProvince(zoneDto.getCityName(), zoneDto.getProvinceName());
        UserAccountZone userAccountZone = UserAccountZone.AddZoneToUserAccount(userAccount, zone);

        userAccount.setZone(userAccountZone);
    }

    @Transactional
    public boolean removeZone(String nickname, String cityName, String provinceName) {
        UserAccount userAccount = userAccountRepository.findByNickname(nickname);
        Zone zone = zoneRepository.findByCityAndProvince(cityName, provinceName);

        Optional<UserAccountZone> userAccountZone = userAccount.getZones()
                .stream()
                .filter(UserZone -> UserZone
                        .getZone()
                        .toString()
                        .equals(zone.toString()))
                .findFirst();

        userAccountZone.ifPresent(userZone -> userAccount.getZones().remove(userZone));

        return userAccountZone.isPresent();
    }
}
