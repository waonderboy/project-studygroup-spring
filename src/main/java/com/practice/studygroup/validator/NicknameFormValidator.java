package com.practice.studygroup.validator;

import com.practice.studygroup.dto.request.NicknameForm;
import com.practice.studygroup.dto.request.SignUpForm;
import com.practice.studygroup.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NicknameFormValidator implements Validator {

    private final UserAccountRepository userAccountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(NicknameForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm) target;

        if (userAccountRepository.existsByNickname(nicknameForm.getNewNickname())){
            errors.rejectValue("email",  "wrong.nickname", new Object[]{nicknameForm.getNewNickname()}, "이미 사용중인 닉네임입니다.");
        }
    }
}
