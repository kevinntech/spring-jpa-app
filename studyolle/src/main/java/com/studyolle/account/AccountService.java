package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    private Account saveNewAccount(SignUpForm signUpForm) {
        // 회원 생성
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword())) // 패스워드를 인코딩한다.
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .build();

        // 회원 저장
        // accountRepository.save() 안에서는 트랜잭션 처리가 되며 account는 persist 상태이다.
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

    @Transactional // 트랜잭션 처리
    public Account processNewAccount(SignUpForm signUpForm) {
        // (1) 새로운 회원을 생성해서 저장한다.
        //     여기서는 트랜잭션의 범위를 벗어났기 때문에 account는 detached 상태이다.
        //     이 문제를 해결하려면 processNewAccount()에 @Transactional로 트랜잭션 처리를 해주어야 한다.
        Account newAccount = saveNewAccount(signUpForm);

        // (2) 이메일 인증 토큰을 생성한다.
        newAccount.generateEmailCheckToken();

        // (3) 가입 확인 이메일을 전송한다.
        sendSignUpConfirmEmail(newAccount);

        return newAccount;
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
