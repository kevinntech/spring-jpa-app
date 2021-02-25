package com.studyolle.study;

import com.studyolle.account.CurrentAccount;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.study.form.StudyForm;
import com.studyolle.study.validator.StudyFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final StudyRepository studyRepository;
    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final StudyFormValidator studyFormValidator;

    @InitBinder("studyForm")
    public void studyFormInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(studyFormValidator);
    }

    @GetMapping("/new-study")
    public String newStudyForm(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new StudyForm());

        return "study/form";
    }

    @PostMapping("/new-study")
    public String newStudySubmit(@CurrentAccount Account account, @Valid StudyForm studyForm, Errors errors){
        // 에러가 있다면 폼을 다시 보여주기
        if (errors.hasErrors()){
            return "study/form";
        }

        Study newStudy = studyService.createNewStudy(modelMapper.map(studyForm, Study.class), account);
        return "redirect:/study/" + URLEncoder.encode(newStudy.getPath(), StandardCharsets.UTF_8); // 새롭게 생성한 스터디의 정보를 표시하는 뷰를 보여준다.
    }

    @GetMapping("/study/{path}")
    public String viewStudy(@CurrentAccount Account account, @PathVariable String path, Model model){
        model.addAttribute(account);
        model.addAttribute(studyRepository.findByPath(path));

        return "study/view";
    }


}
