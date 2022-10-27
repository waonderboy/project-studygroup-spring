package com.practice.studygroup.dto.security;

import com.practice.studygroup.domain.UserAccount;
import com.practice.studygroup.dto.UserAccountDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter @Builder
@AllArgsConstructor
public class CommonUserPrincipal implements UserDetails {

    private String email;
    private String password;
    private String nickname;
    private Collection<? extends GrantedAuthority> authorities;

    private static CommonUserPrincipal of(String email, String password, String nickname) {
        Set<RoleType> roleTypes = Set.of(RoleType.USER);
        return CommonUserPrincipal.builder()
                .email(email)
                .password(password)
                .authorities(roleTypes.stream()
                        .map(RoleType::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableSet()))
                .nickname(nickname)
                .build();
    }

    public UserAccountDto toDto() {
        return UserAccountDto.builder()
                .nickname(nickname)
                .password(password)
                .nickname(nickname)
                .build();
    }

    public static CommonUserPrincipal from(UserAccountDto dto){
        return CommonUserPrincipal.of(dto.getEmail(), dto.getPassword(), dto.getNickname());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return nickname;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }

}