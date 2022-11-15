package com.practice.studygroup.repository;


import com.practice.studygroup.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {
    boolean existsByPath(String path);

    Study findByPath(String path);
}
