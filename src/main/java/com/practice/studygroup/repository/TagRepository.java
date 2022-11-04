package com.practice.studygroup.repository;


import com.practice.studygroup.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByTitle(String title);

    boolean deleteByTitle(String tagTitle);
}
