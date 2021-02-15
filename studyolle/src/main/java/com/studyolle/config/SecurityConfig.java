package com.studyolle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests() // 요청에 대한 권한을 지정한다.
                // 아래 요청은 권한 확인 없이 접근 가능해야 한다.
                .mvcMatchers("/", "/login", "/sign-up", "/check-email", "/check-email-token",
                            "/email-login", "/check-email-login", "/login-link").permitAll()
                // 프로필 요청은 HTTP GET 요청만 허용한다.
                .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll()
                // 나머지는 로그인을 해야 사용 할 수 있다.
                .anyRequest().authenticated();

    }
}
