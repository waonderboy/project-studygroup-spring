package com.practice.studygroup.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter @Builder
@EqualsAndHashCode(of = "id")
@Entity @AllArgsConstructor
public class Tag {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;

    protected Tag() {}

    public static Tag of(String title) {
        return Tag.builder().title(title).build();
    }
}
