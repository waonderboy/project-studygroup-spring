package com.practice.studygroup.config;

import com.practice.studygroup.domain.UserAccount;
import com.practice.studygroup.dto.security.CommonUserPrincipal;
import com.practice.studygroup.repository.UserAccountRepository;
import com.practice.studygroup.security.service.CommonUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import javax.sql.DataSource;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Import({SecurityConfig.class})
public class TestSecurityConfig {
    @MockBean
    private UserAccountRepository userAccountRepository;

    @MockBean
    private DataSource dataSource;



    @BeforeTestMethod
    public void securitySetUp() {
        UserAccount account = UserAccount.builder()
                .email("testemail12@naver.com")
                .password("aasdf!@#")
                .nickname("nick")
                .build();
        given(userAccountRepository.findByEmail(anyString())).willReturn(Optional.of(account));

    }

}
