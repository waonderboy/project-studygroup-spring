package com.practice.studygroup.domain;

import com.practice.studygroup.dto.UserAccountDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Builder @AllArgsConstructor
@ToString @EqualsAndHashCode(of = "id")
public class UserAccount {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(unique = true)
    private String nickname;

    private int age;

    private String occupation;

    private String location;

    private String memo;

    private String url;

    private String bio;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean emailVerified;

    private String emailCheckToken;
    private LocalDateTime emailCheckTokenCreatedAt;

    private LocalDateTime createdAt;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    protected UserAccount() {
    }

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenCreatedAt = LocalDateTime.now();
    }

    public boolean completeSignUp() {
        this.emailVerified = true;
        this.createdAt = LocalDateTime.now();
        return true;
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canResendToken() {
        return emailCheckTokenCreatedAt.isBefore(LocalDateTime.now().minusMinutes(3));
    }

    public void changeProfile(String bio, String url, String occupation, String location) {
        this.bio = bio;
        this.url = url;
        this.occupation = occupation;
        this.location = location;
    }
}
