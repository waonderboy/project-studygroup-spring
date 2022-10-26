package com.practice.studygroup.dto.request;


import com.practice.studygroup.domain.UserAccount;
import com.practice.studygroup.dto.UserAccountDto;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpForm {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
    private String nickname;

    @NotBlank
    @Length(min = 8, max = 50)
    private String password;

    public static SignUpForm of() {
        return new SignUpForm();
    }

    public static SignUpForm of(String email, String nickname, String password) {
        return new SignUpForm(email, nickname, password);
    }



    // TODO :: need to Method making DTO because open session in view
    public UserAccountDto toDto() {
        return UserAccountDto.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

}
