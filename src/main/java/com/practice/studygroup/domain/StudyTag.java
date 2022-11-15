package com.practice.studygroup.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;

@Getter @Builder
@EqualsAndHashCode(of = "id")
@Entity @AllArgsConstructor
public class StudyTag {

    @Column(name = "study_tag_id")
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    protected StudyTag() {}


}
