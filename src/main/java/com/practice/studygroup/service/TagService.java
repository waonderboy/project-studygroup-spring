package com.practice.studygroup.service;

import com.practice.studygroup.domain.Tag;
import com.practice.studygroup.domain.UserAccount;
import com.practice.studygroup.domain.UserAccountTag;
import com.practice.studygroup.dto.TagDto;
import com.practice.studygroup.repository.TagRepository;
import com.practice.studygroup.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;

    private final UserAccountRepository userAccountRepository;

    @Transactional
    public TagDto findOrCreateNew(String tagTitle) {
        return TagDto.from(tagRepository.findByTitle(tagTitle)
                .orElseGet(() -> tagRepository.save(Tag.of(tagTitle))));
    }


    public List<String> getAllTags() {
        return tagRepository.findAll().stream()
                .map(Tag::getTitle)
                .collect(Collectors.toList());
    }
}
