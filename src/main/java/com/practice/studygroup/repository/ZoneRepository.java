package com.practice.studygroup.repository;


import com.practice.studygroup.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone,Long> {
    Zone findByCityAndProvince(String city, String province);
}
