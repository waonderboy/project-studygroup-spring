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

    @Column(name = "useraccount_zone_id")
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useraccount_id")
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone;

    protected UserAccountZone() {}

    public static UserAccountZone AddZoneToUserAccount(UserAccount userAccount, Zone zone) {
        return UserAccountZone.builder()
                .userAccount(userAccount)
                .zone(zone)
                .build();
    }
}
