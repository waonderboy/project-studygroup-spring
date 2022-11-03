package com.practice.studygroup.service;

import com.practice.studygroup.domain.Tag;
import com.practice.studygroup.dto.TagDto;
import com.practice.studygroup.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public TagDto findOrCreateNew(String tagTitle) {
        return TagDto.from(tagRepository.findByTitle(tagTitle)
                .orElseGet(() -> tagRepository.save(Tag.of(tagTitle))));
    }
}
