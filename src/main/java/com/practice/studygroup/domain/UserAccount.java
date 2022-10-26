package com.practice.studygroup.domain;

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

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime createdAt;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    protected UserAccount() {
    }

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }
}
