package com.studyolle.account;

import com.studyolle.domain.Account;
import com.studyolle.settings.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

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

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        // 처음에는 이메일을 입력할 것이라 가정한다.
        Account account = accountRepository.findByEmail(emailOrNickname);

        // 이메일로 유저를 찾았을 때, null이면
        if(account == null){
            // 닉네임으로 유저를 한번 더 찾아본다.
            account = accountRepository.findByNickname(emailOrNickname);
        }

        // 그래도 account가 null이면 UsernameNotFoundException 예외를 발생 시킨다.
        if(account == null){
            throw new UsernameNotFoundException(emailOrNickname);
        }

        // 위의 과정을 통과 했다면 유저가 있다는 것이므로 Principal에 해당하는 객체(UserAccount)를 반환하면 된다.
        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

    public void updateProfile(Account account, Profile profile) {
        account.setUrl(profile.getUrl());
        account.setOccupation(profile.getOccupation());
        account.setLocation(profile.getLocation());
        account.setBio(profile.getBio());

        // TODO 프로필 이미지

        accountRepository.save(account);

        // TODO 처리하지 못한 문제가 하나 더 남았다. (프로필 이미지 변경 시 발견 될 예정)
    }
}
