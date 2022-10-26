package com.practice.studygroup.dto;


import com.practice.studygroup.domain.UserAccount;
import lombok.*;

@EqualsAndHashCode(of = "email")
@Getter @Setter @Builder
@AllArgsConstructor
public class UserAccountDto {
    private String email;
    private String password;
    private String nickname;


    public UserAccount toEntity() {
        return UserAccount.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }
}
