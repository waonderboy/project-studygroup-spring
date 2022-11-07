package com.practice.studygroup.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.studygroup.WithAccount;
import com.practice.studygroup.domain.Tag;
import com.practice.studygroup.domain.UserAccount;
import com.practice.studygroup.dto.TagDto;
import com.practice.studygroup.dto.ZoneDto;
import com.practice.studygroup.dto.request.PasswordForm;
import com.practice.studygroup.dto.request.TagForm;
import com.practice.studygroup.dto.request.ZoneForm;
import com.practice.studygroup.repository.TagRepository;
import com.practice.studygroup.repository.UserAccountRepository;
import com.practice.studygroup.repository.ZoneRepository;
import com.practice.studygroup.service.TagService;
import com.practice.studygroup.service.UserAccountService;
import com.practice.studygroup.service.ZoneService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 단위 테스트로 전환 검토
 */
@Transactional
@DisplayName("세팅 컨트롤러 - 수정")
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TagService tagService;
    @Autowired
    private ZoneRepository zoneRepository;
    @Autowired
    private ZoneService zoneService;

    @AfterEach
    void afterEach() {
        userAccountRepository.deleteAll();
    }

    @WithAccount("testNickName")
    @DisplayName("[Post] 프로필 수정 - 정상 요청")
    @Test
    void updateProfile() throws Exception {
        String bio = "change bio";
        mockMvc.perform(post("/settings/profile")
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/" + "testNickName"));

        UserAccount userAccount = userAccountRepository.findByNickname("testNickName");
        assertEquals(bio, userAccount.getBio());
    }

    @WithAccount("testNickName")
    @DisplayName("[Post] 프로필 수정 - 입력값 오류")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 너무나도 길게 소개를 수정하는 경우. ";
        mockMvc.perform(post("/settings/profile")
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        UserAccount userAccount = userAccountRepository.findByNickname("testNickName");
        assertNull(userAccount.getBio());
    }

    @WithAccount("testNickName")
    @DisplayName("[Post] 패스워드 수정 - 정상")
    @Test
    void updatePassword() throws Exception {
        String newPassword = "123456781";
        mockMvc.perform(post("/settings/password")
                        .param("newPassword", newPassword)
                        .param("newPasswordConfirm", newPassword)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/" + "testNickName"));

        String changedPassword = userAccountRepository.findByNickname("testNickName").getPassword();
        assertTrue(passwordEncoder.matches(newPassword, changedPassword));

    }

    @WithAccount("testNickName")
    @DisplayName("[Post] 패스워드 수정 - 입력값 오류")
    @Test
    void updatePassword_error() throws Exception {
        String newPassword = "123456781";
        mockMvc.perform(post("/settings/password")
                        .param("newPassword", newPassword)
                        .param("newPasswordConfirm", "123124")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().hasErrors());
        
    }

    @WithAccount("testNickName")
    @DisplayName("[GET] 태그 수정 폼 - 성공")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get("/settings/tags"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/tags"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));

    }
    @WithAccount("testNickName")
    @DisplayName("[Post] 태그 추가 - 성공")
    @Test
    void addTags() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("new");
        mockMvc.perform(post("/settings/tags/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertNotNull(tagRepository.findByTitle("new"));
        assertThat(userAccountRepository.findByNickname("testNickName").getTags().stream()
                .map(userAccountTag -> userAccountTag.getTag().getTitle())
                .collect(Collectors.toList()))
                .contains("new");
    }

    @Disabled
    @WithAccount("testNickName")
    @DisplayName("[Post] 태그 삭제 - 성공")
    @Test
    void removeTags() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("new");
        TagDto tagDto = tagService.findOrCreateNew("new");
        userAccountService.addTag("testNickName", tagDto);

        mockMvc.perform(post("/settings/tags/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertThat(userAccountRepository.findByNickname("testNickName").getTags().stream()
                .map(userAccountTag -> userAccountTag.getTag().getTitle())
                .collect(Collectors.toList()))
                .doesNotContain("new");
    }

    @WithAccount("testNickName")
    @DisplayName("[GET] 지역 수정 폼 - 성공")
    @Test
    void updateZonesForm() throws Exception {
        mockMvc.perform(get("/settings/zones"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/zones"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));

    }
    @WithAccount("testNickName")
    @DisplayName("[Post] 지역 추가 - 성공")
    @Test
    void addZones() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName("Andong(안동시)/North Gyeongsang");
        mockMvc.perform(post("/settings/zones/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertNotNull(zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName()));
        assertThat(userAccountRepository.findByNickname("testNickName").getZones().stream()
                .map(userAccountZone -> userAccountZone.getZone().toString())
                .collect(Collectors.toList()))
                .contains("Andong(안동시)/North Gyeongsang");
    }


    @Disabled
    @WithAccount("testNickName")
    @DisplayName("[Post] 지역 삭제 - 성공")
    @Test
    void removeZones() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName("Andong(안동시)/North Gyeongsang");
        ZoneDto zoneDto = zoneService.searchZone(zoneForm.getCityName(), zoneForm.getProvinceName());
        userAccountService.addZone("testNickName", zoneDto);

        mockMvc.perform(post("/settings/zones/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertThat(userAccountRepository.findByNickname("testNickName").getZones().stream()
                .map(userAccountZone -> userAccountZone.getZone().toString())
                .collect(Collectors.toList()))
                .doesNotContain("Andong(안동시)/North Gyeongsang");

    }
}