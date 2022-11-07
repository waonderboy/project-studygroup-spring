package com.practice.studygroup.dto;

import com.practice.studygroup.domain.Tag;
import com.practice.studygroup.domain.Zone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZoneDto {

    private String cityName;

    private String provinceName;

    private String localNameOfCity;

    public static ZoneDto from(Zone entity) {
        return ZoneDto.builder()
                .cityName(entity.getCity())
                .provinceName(entity.getProvince())
                .localNameOfCity(entity.getLocalNameOfCity())
                .build();
    }

}
