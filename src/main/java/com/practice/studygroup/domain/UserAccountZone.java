package com.practice.studygroup.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;

@Getter @Builder
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@Entity
public class UserAccountZone {

    @Column(name = "USERACCOUNT_ZONE_ID")
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERACCOUNT_ID")
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ZONE_ID")
    private Zone zone;

    protected UserAccountZone() {}

    public static UserAccountZone AddZoneToUserAccount(UserAccount userAccount, Zone zone) {
        return UserAccountZone.builder()
                .userAccount(userAccount)
                .zone(zone)
                .build();
    }
}
