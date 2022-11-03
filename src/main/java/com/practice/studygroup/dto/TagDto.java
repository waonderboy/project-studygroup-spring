package com.practice.studygroup.dto;

import com.practice.studygroup.domain.Tag;
import lombok.*;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagDto {

    private String tagTitle;

    public static TagDto from(Tag entity) {
        return TagDto.builder()
                .tagTitle(entity.getTitle())
                .build();
    }

}
