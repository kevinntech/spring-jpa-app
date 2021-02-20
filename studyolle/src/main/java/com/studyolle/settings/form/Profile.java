package com.studyolle.settings.form;

import com.studyolle.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
public class Profile {

    // 짧은 자기소개
    @Length(max = 35)
    private String bio;

    // 자신의 블로그, SNS url
    @Length(max = 50)
    private String url;

    // 직업
    @Length(max = 50)
    private String occupation;

    private String profileImage;

    // 살고 있는 지역
    @Length(max = 50)
    private String location;

}
