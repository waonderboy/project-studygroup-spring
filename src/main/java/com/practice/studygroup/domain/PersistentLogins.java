package com.practice.studygroup.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Table(name = "persistent_logins")
@Entity
@Getter @Setter
public class PersistentLogins {

    @Id
    @Column(length = 64)
    private String series;

    @Column(length = 64)
    private String username;

    @NotNull
    @Column(length = 64)
    private String token;

    @NotNull
    @Column(name = "last_used")
    private LocalDateTime lastUsed;
}
