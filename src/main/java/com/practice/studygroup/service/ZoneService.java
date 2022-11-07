package com.practice.studygroup.service;

import com.practice.studygroup.domain.Tag;
import com.practice.studygroup.domain.Zone;
import com.practice.studygroup.dto.TagDto;
import com.practice.studygroup.dto.ZoneDto;
import com.practice.studygroup.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    // 빈이 초기화 되고 실행됨
    @PostConstruct
    public void  initZoneData() throws IOException {
        if (zoneRepository.count() == 0) {
            Resource resource = new ClassPathResource("zone_kr.csv");
            List<Zone> zoneList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                    .map(line -> {
                        String[] split = line.split(",");
                        return Zone.builder()
                                .city(split[0])
                                .localNameOfCity(split[1])
                                .province(split[2])
                                .build();
                    }).collect(Collectors.toList());

            zoneRepository.saveAll(zoneList);
        }


    }

    public List<String> getAllZones() {
        return zoneRepository.findAll().stream()
                .map(Zone::toString)
                .collect(Collectors.toList());
    }

    public ZoneDto searchZone(String cityName, String provinceName) {
        Zone zone = zoneRepository.findByCityAndProvince(cityName, provinceName);
        System.out.println("zone = " + zone);
        if (zone == null) {
            return null;
        }
        return ZoneDto.from(zone);

    }
}
