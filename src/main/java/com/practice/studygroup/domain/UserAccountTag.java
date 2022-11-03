package com.practice.studygroup.domain;

import lombok.*;

import javax.persistence.*;

@Getter @Builder
@EqualsAndHashCode(of = "id")
@Entity @AllArgsConstructor
public class UserAccountTag {

    @Column(name = "USERACCOUNT_TAG_ID")
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERACCOUNT_ID")
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAG_ID")
    private Tag tag;

    protected UserAccountTag() {}

    public static UserAccountTag AddTagToUserAccount(UserAccount userAccount, Tag tag) {
        return UserAccountTag.builder()
                .userAccount(userAccount)
                .tag(tag)
                .build();
    }
}
