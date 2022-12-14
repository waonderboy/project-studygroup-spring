package com.practice.studygroup.dto.request;

import com.practice.studygroup.dto.StudyDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyForm {

    public static final String VALID_PATH_PATTERN = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$";

    @NotBlank
    @Length(min = 2, max = 20)
    @Pattern(regexp = VALID_PATH_PATTERN)
    private String path;

    @NotBlank
    @Length(max = 50)
    private String title;

    @NotBlank
    @Length(max = 100)
    private String shortDescription;

    @NotBlank
    private String fullDescription;


    public static StudyForm of() {
        return new StudyForm();
    }

    public StudyDto toDto() {
        return StudyDto.builder()
                .path(path)
                .title(title)
                .shortDescription(shortDescription)
                .fullDescription(fullDescription)
                .build();
    }

}
