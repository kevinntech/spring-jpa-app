package com.studyolle.settings;

import com.studyolle.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Profile {

    // 짧은 자기소개
    private String bio;

    // 자신의 블로그, SNS url
    private String url;

    // 직업
    private String occupation;

    // 살고 있는 지역
    private String location;

    public Profile(Account account) {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }
}
