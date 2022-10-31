package com.practice.studygroup.dto.response;

import com.practice.studygroup.domain.UserAccount;
import com.practice.studygroup.dto.UserAccountDto;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;


@EqualsAndHashCode(of = "email")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileForm {

    private String email;
    private String nickname;

    @Length(max = 35)
    private String bio;

    @Length(max = 50)
    private String url;

    @Length(max = 50)
    private String occupation;

    @Length(max = 50)
    private String location;

    private String profileImage;

    private boolean emailVerified;

    private LocalDateTime createdAt;

    public static ProfileForm from(UserAccount entity) {
        return ProfileForm.builder()
                .email(entity.getEmail())
                .nickname(entity.getNickname())
                .occupation(entity.getOccupation())
                .location(entity.getLocation())
                .profileImage(entity.getProfileImage())
                .bio(entity.getBio())
                .url(entity.getUrl())
                .emailVerified(entity.isEmailVerified())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public UserAccount toEntity() {
        return UserAccount.builder()
                .occupation(occupation)
                .location(location)
                .profileImage(profileImage)
                .bio(bio)
                .url(url)
                .build();
    }
}
