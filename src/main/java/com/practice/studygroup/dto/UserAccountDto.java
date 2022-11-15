package com.practice.studygroup.dto;


import com.practice.studygroup.domain.UserAccount;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;

@EqualsAndHashCode(of = "email")
@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountDto{
    private Long id;
    private String email;
    private String password;
    private String nickname;

    private boolean emailVerified;

    private String profileImage;


    public UserAccount toEntity(PasswordEncoder passwordEncoder) {
        return UserAccount.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .build();
    }

    public static UserAccountDto from(UserAccount entity) {
        return UserAccountDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .nickname(entity.getNickname())
                .profileImage(entity.getProfileImage())
                .emailVerified(entity.isEmailVerified())
                .build();
    }


}
