package com.studyolle.account;

import com.studyolle.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private AccountRepository accountRepository;

    /*
    * @MockBean를 사용해서 Mocking 한다.
    * 외부 연동은 Mocking으로 처리해서 쉽게 테스트 할 수 있다.
    * */
    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up")) // 뷰 이름이 "account/sign-up"인지 확인
                .andExpect(model().attributeExists("signUpForm")); // 모델 정보에 signUpForm 애트리뷰트가 있는지 확인
    }

    /*
    * 프론트엔드 코드를 뚫고 잘못된 값이 입력되었다고 가정한 테스트 코드
    * */
    @DisplayName("회원 가입 처리 - 입력 값 오류")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                    .param("nickname", "kevin")
                    .param("email", "email..")
                    .param("password", "12345")
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @DisplayName("회원 가입 처리 - 입력 값 정상")
    @Test
    void signUpSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "kevin")
                .param("email", "kevin@email.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        Account account = accountRepository.findByEmail("kevin@email.com");

        assertNotNull(account);

        /*
        * 사용자가 입력한 패스워드(12345678)와 회원에 저장된 패스워드를 비교 했을 때,
        * 다르다면 그것은 암호화가 되었다는 것으로 판단하고 테스트 코드를 작성한다.
        * */
        assertNotEquals(account.getPassword(), "12345678");
        assertNotNull(account.getEmailCheckToken()); // 토큰이 null이 아닌지 확인한다.
        // 어떤 SimpleMailMessage 타입이든 send()가 호출 되었는지 확인한다.
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

}