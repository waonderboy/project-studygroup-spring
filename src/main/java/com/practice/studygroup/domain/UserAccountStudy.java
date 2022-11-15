package com.practice.studygroup.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import java.util.Optional;

@Getter @Builder
@EqualsAndHashCode(of = "id")
@Entity @AllArgsConstructor
public class UserAccountStudy {

    @Column(name = "useraccount_study_id")
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useraccount_id")
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    private StudyGroupRole role;

    protected UserAccountStudy() {}

    public static UserAccountStudy AddMemberToStudy(UserAccount userAccount, Study study) {
        return UserAccountStudy.builder()
                .userAccount(userAccount)
                .study(study)
                .role(StudyGroupRole.MANAGER)
                .build();
    }
}
