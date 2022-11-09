package com.practice.studygroup.domain;

import lombok.*;

import javax.persistence.*;

@Getter @Builder
@EqualsAndHashCode(of = "id")
@Entity @AllArgsConstructor
public class UserAccountTag {

    @Column(name = "useraccount_tag_id")
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useraccount_id")
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    protected UserAccountTag() {}

    public static UserAccountTag AddTagToUserAccount(UserAccount userAccount, Tag tag) {
        return UserAccountTag.builder()
                .userAccount(userAccount)
                .tag(tag)
                .build();
    }
}
