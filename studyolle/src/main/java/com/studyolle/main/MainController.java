package com.studyolle.main;

import com.studyolle.account.CurrentAccount;
import com.studyolle.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }

        return "index";
    }

    // 로그인 페이지를 보여준다.
    @GetMapping("/login")
    public String login(){
        return "login";
    }

}
