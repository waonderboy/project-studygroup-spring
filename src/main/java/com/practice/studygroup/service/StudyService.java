package com.practice.studygroup.service;

import com.practice.studygroup.domain.Study;
import com.practice.studygroup.domain.UserAccount;
import com.practice.studygroup.domain.UserAccountStudy;
import com.practice.studygroup.dto.StudyDto;
import com.practice.studygroup.dto.UserAccountDto;
import com.practice.studygroup.repository.StudyRepository;
import com.practice.studygroup.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {
    private final StudyRepository studyRepository;
    private final UserAccountRepository userAccountRepository;
    @Transactional
    public StudyDto createNewStudy(StudyDto studyDto, UserAccountDto dto) {

        UserAccount account = userAccountRepository.getReferenceById(dto.getId());
        Study newStudy = studyRepository.save(studyDto.toEntity());
        UserAccountStudy userAccountStudy = UserAccountStudy.AddMemberToStudy(account, newStudy);
        newStudy.addMember(userAccountStudy);

        return StudyDto.from(newStudy);
    }

    public StudyDto getStudy(String path) {
        Study study = studyRepository.findByPath(path);
        if (study == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
        }
        return StudyDto.from(study);
    }
}
