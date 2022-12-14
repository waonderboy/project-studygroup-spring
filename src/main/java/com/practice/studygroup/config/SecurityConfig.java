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
                .rememberMe().userDetailsService(userDetailsService())
                    .tokenRepository(tokenRepository())
                .and()
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CommonUserDetailService(userAccountRepository);
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
     * SecurityContextHolder??? Authentication(principal, credentials, authorities)??? ???????????????
     * Authentication?????? ?????? ??? ?????????????????? ??????
     * ????????? ?????? ????????????
     */
//    @Bean
//    public AuthenticationFilter authenticationFilter() {
//        return null;
//
//        //TODO: Token, SecurityContextHolder
//    }

    /**
     * ?????? ?????????????????? ??????????????? ???????????? ??????
     * @Return authentication
     */
//    @Bean
//    public AuthenticationManager authenticationManager() {
//        return null;
//    }

    /**
     * ???????????? ?????? *??????*??? ???????????? ??? AuthenticationManager ?????? ??????
     * @Return authentication
     */
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        return new CommonAuthenticationProvider(userDetailsService());
//    }

    /**
     * ?????? *??????*??? ???????????????
     * UserDetails??? ????????? ???, UserDetails??? Dto??? ???????????? ???????????? ?????? ??? ??????
     * @return UserDetails <- ????????? ?????? principal ???????????? (????????? ?????????, ???????????????, ?????? ??????)
     */
//    @Bean
//    public UserDetailService userDetailService() {
//        return new CommonAuthenticationProvider(userDetailsService());
//    }
}
