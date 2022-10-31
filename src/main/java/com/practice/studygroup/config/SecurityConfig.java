package com.practice.studygroup.config;

import com.practice.studygroup.repository.UserAccountRepository;
import com.practice.studygroup.security.service.CommonUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final DataSource dataSource;

    private final UserAccountRepository userAccountRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeRequests(auth -> auth
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .mvcMatchers(
                            "/sign-up",
                            "/home"
                    ).permitAll()
                    .mvcMatchers(HttpMethod.POST, "/sign-up")
                    .permitAll())
                .formLogin()
                    .loginPage("/sign-in")
                    .permitAll()
                    .and()
                .logout()
                    .logoutSuccessUrl("/")
                    .and()
                .rememberMe().userDetailsService(userDetailsService)
                    .tokenRepository(tokenRepository())
                .and()
                .build();
    }


    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);

        return jdbcTokenRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * SecurityContextHolder에 Authentication(principal, credentials, authorities)를 저장하거나
     * Authentication토큰 생성 후 인증로직으로 전달
     * 로그인 여부 파악가능
     */
//    @Bean
//    public AuthenticationFilter authenticationFilter() {
//        return null;
//
//        //TODO: Token, SecurityContextHolder
//    }

    /**
     * 여러 프로바이더로 인증로직을 실행할수 잇음
     * @Return authentication
     */
//    @Bean
//    public AuthenticationManager authenticationManager() {
//        return null;
//    }

    /**
     * 구체적인 인증 *로직*을 수행하는 곳 AuthenticationManager 안에 있음
     * @Return authentication
     */
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        return new CommonAuthenticationProvider(userDetailsService());
//    }

    /**
     * 인증 *정보*를 획득하는곳
     * UserDetails를 만드는 곳, UserDetails는 Dto가 상속받아 구현체로 만들 수 있음
     * @return UserDetails <- 상황에 맞는 principal 부여가능 (어드민 페이지, 유저게시판, 접근 구역)
     */
//    @Bean
//    public UserDetailService userDetailService() {
//        return new CommonAuthenticationProvider(userDetailsService());
//    }
}
