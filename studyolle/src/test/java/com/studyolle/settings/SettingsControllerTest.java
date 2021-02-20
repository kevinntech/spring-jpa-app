package com.studyolle.settings;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired AccountRepository accountRepository;

    @Autowired PasswordEncoder passwordEncoder;

    // 매번 테스트가 끝난 다음에는 계정을 삭제
    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    /*
    * 아래의 요청 URL는 인증된 사용자만 접근 할 수 있기 때문에
    * @WithAccount이 없다면 스프링 시큐리티가 인증이 필요한 페이지이기 때문에 login 페이지로 리다이렉트를 한다.
    * @WithAccount로 인증 정보를 제공해야 "해당 사용자를 인증된 사용자"로 판단하고 요청이 정상적으로 처리된다.
    * */
    @WithAccount("kevin")
    @DisplayName("프로필 수정 폼 보여주기")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    /*
    * @WithAccount("이름")를 사용하면 이름에 해당하는 계정(Account)를 만들기 때문에
    * 매번 테스트가 끝난 다음에는 계정을 삭제해야 된다. 그러면 @WithAccount를 여러 번 사용하더라도 문제가 없다.
    * */
    @WithAccount("kevin")
    @DisplayName("프로필 수정하기 - 입력 값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf())) // 폼을 제출하는 테스트 코드를 작성할 때는 csrf 토큰을 같이 전달해야 한다.
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account kevin = accountRepository.findByNickname("kevin");

        assertEquals(bio, kevin.getBio());
    }

    @WithAccount("kevin")
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_error() throws Exception {
        /*
        * 짧은 소개가 35자가 넘는 경우에는 에러가 발생하게 되어 있다.
        * 단, Profile 클래스에서 JSR 303 애노테이션으로 검증을 적용한 상태여야 한다.
        * */
        String bio = "길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 너무나도 길게 소개를 수정하는 경우. ";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                    .param("bio", bio)
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors()); // 모델에 에러가 담겨 있다.

        Account kevin = accountRepository.findByNickname("kevin");
        assertNull(kevin.getBio());
    }

    @WithAccount("kevin")
    @DisplayName("패스워드 수정 폼")
    @Test
    void updatePassword_form() throws Exception {
        /*
         * SETTINGS_PASSWORD_URL로 요청 했을 때, 상태 값이 OK 인지 모델에 뷰를 보여줄 때
         * 필요한 정보(account, passwordForm)가 있는지 확인한다.
         * */
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("kevin")
    @DisplayName("패스워드 수정 - 입력값 정상")
    @Test
    void updatePassword_success() throws Exception {
        /*
         * SETTINGS_PASSWORD_URL로 POST 요청 했을 때, 입력 값(newPassword,newPasswordConfirm)이 같도록 함
         * csrf 토큰을 넣어준다. 그리고 상태 값은 리다이렉션 상태일 것이며 리다이렉트되는 URL은 SETTINGS_PASSWORD_URL이며
         * flashAttribute에 message가 있는지 확인한다.
         * */
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        /*
         * 마지막으로는 passwordEncoder를 주입 받은 다음, 실제 변경된 패스워드가 매치되는지 확인한다.
         * */
        Account kevin = accountRepository.findByNickname("kevin");
        assertTrue(passwordEncoder.matches("12345678", kevin.getPassword()));
    }

    @WithAccount("kevin")
    @DisplayName("패스워드 수정 - 입력값 에러 - 패스워드 불일치")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "11111111")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }

}