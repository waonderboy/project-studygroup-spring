package com.practice.studygroup.domain;

import com.practice.studygroup.dto.UserAccountDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    private boolean studyUpdateResultByEmail;

    private boolean studyUpdateResultByWeb;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserAccountTag> tags = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserAccountZone> zones = new HashSet<>();


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

    public void changeProfile(String bio, String url, String occupation, String location, String profileImage) {
        this.bio = bio;
        this.url = url;
        this.occupation = occupation;
        this.location = location;
        this.profileImage = profileImage;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeNotification(boolean studyCreatedByEmail,
                                   boolean studyCreatedByWeb,
                                   boolean studyEnrollmentResultByEmail,
                                   boolean studyEnrollmentResultByWeb,
                                   boolean studyUpdateResultByEmail,
                                   boolean studyUpdateResultByWeb) {
        this.studyCreatedByEmail = studyCreatedByEmail;
        this.studyCreatedByWeb = studyCreatedByWeb;
        this.studyEnrollmentResultByEmail = studyEnrollmentResultByEmail;
        this.studyEnrollmentResultByWeb = studyEnrollmentResultByWeb;
        this.studyUpdateResultByEmail = studyUpdateResultByEmail;
        this.studyUpdateResultByWeb = studyUpdateResultByWeb;
    }

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void setTag(UserAccountTag userAccountTag) {
        this.tags.add(userAccountTag);
    }


    public void setZone(UserAccountZone userAccountZone) {
        this.zones.add(userAccountZone);
    }
}
