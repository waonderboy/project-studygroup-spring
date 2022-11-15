package com.practice.studygroup.dto;


import com.practice.studygroup.domain.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyDto {
    private String path;
    private String title;
    private String shortDescription;
    private String fullDescription;
    private String image;
    private boolean useBanner;

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdatedDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private int memberCount;

    private Set<UserAccountDto> members = new HashSet<>();

    private Set<TagDto> tags = new HashSet<>();

    private Set<ZoneDto> zones = new HashSet<>();

    public static StudyDto from(Study entity) {
        return StudyDto.builder()
                .path(entity.getPath())
                .title(entity.getTitle())
                .shortDescription(entity.getShortDescription())
                .fullDescription(entity.getFullDescription())
                .useBanner(entity.isUseBanner())
                .publishedDateTime(entity.getPublishedDateTime())
                .closedDateTime(entity.getClosedDateTime())
                .recruitingUpdatedDateTime(entity.getRecruitingUpdatedDateTime())
                .recruiting(entity.isRecruiting())
                .published(entity.isPublished())
                .closed(entity.isClosed())
                .memberCount(entity.getMemberCount())
                .members(entity.getMembers().stream().map(e -> UserAccountDto.from(e.getUserAccount())).collect(Collectors.toSet()))
                .build();
//                .tags(entity.getTags().stream().map(TagDto::from).collect(Collectors.toSet()))
//                .zones(entity.getZones().stream())

    }

    public Study toEntity() {
        return Study.builder()
                .path(path)
                .title(title)
                .shortDescription(shortDescription)
                .fullDescription(fullDescription)
                .useBanner(useBanner)
                .image(image)
                .members(new HashSet<>())
                .build();
    }
}
