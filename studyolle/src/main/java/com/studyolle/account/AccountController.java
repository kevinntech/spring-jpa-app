package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    // signUpForm 데이터를 받을 때, 사용 할 바인더 및 Validator 설정
    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up") // 파라미터에 @ModelAttribute가 생략된 것
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors){
        if(errors.hasErrors()){
            return "account/sign-up";
        }

        // 회원 가입 처리
        accountService.processNewAccount(signUpForm);

        return "redirect:/";
    }


    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model){
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";

        // 에러가 있다면 모델 정보에 error 정보를 추가한다.
        if(account == null){
            model.addAttribute("error", "wrong.email");
            return view;
        }

        // 회원의 토큰 정보와 전달 받은 토큰 정보가 일치하지 않으면 모델 정보에 error 정보를 추가한다.
        if(!account.getEmailCheckToken().equals(token)){
            model.addAttribute("error", "wrong.token");
            return view;
        }

        // 회원 가입을 완료한다.
        account.completeSignUp();

        model.addAttribute("numberOfUser", accountRepository.count()); // 현재 회원 수
        model.addAttribute("nickname", account.getNickname()); // 닉네임

        return view;
    }

}
