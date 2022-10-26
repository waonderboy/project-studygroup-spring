package com.practice.studygroup.repository;

import com.practice.studygroup.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
