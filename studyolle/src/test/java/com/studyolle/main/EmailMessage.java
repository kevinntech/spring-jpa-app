package com.studyolle.main;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailMessage {

    private String to;  // 받는 사람의 이메일 주소

    private String subject; // 제목

    private String message; // 내용

}