package com.studyolle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        // PasswordEncoder를 빈으로 등록하면 사실상, BCryptPasswordEncoder를 사용하게 된다.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
