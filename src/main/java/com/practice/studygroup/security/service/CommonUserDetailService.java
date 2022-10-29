package com.practice.studygroup.security.service;

import com.practice.studygroup.domain.UserAccount;
import com.practice.studygroup.dto.UserAccountDto;
import com.practice.studygroup.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommonUserDetailService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByEmail(emailOrNickname)
                .orElse(userAccountRepository.findByNickname(emailOrNickname).orElse(null));

        if (userAccount == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }

        return CommonUserPrincipal.from(userAccount);
    }


}
