package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    private Account saveNewAccount(SignUpForm signUpForm) {
        // 회원 생성
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(signUpForm.getPassword()) // TODO 패스워드 인코딩 필요
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .build();

        // 회원 저장
        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디올래, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail() );

        javaMailSender.send(mailMessage);
    }

    public void processNewAccount(SignUpForm signUpForm) {
        // (1) 새로운 회원을 생성해서 저장한다.
        Account newAccount = saveNewAccount(signUpForm);

        // (2) 이메일 인증 토큰을 생성한다.
        newAccount.generateEmailCheckToken();

        // (3) 가입 확인 이메일을 전송한다.
        sendSignUpConfirmEmail(newAccount);
    }
}
