package com.studyolle.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
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

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();

        /*
         * 토크나이저 설정
         * UNDERSCORE(_)를 사용했을 때에만 nested 객체를 참조하는 것으로 간주하고
         * 그렇지 않은 경우에는 해당 객체의 직속 프로퍼티에 바인딩 한다.
         *
         * 즉 UNDERSCORE(_)로 구분하게 해서 UNDERSCORE가 아닌 이상 하나의 프로퍼티로 간주한다.
         * */
        modelMapper.getConfiguration()
                .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE);

        return modelMapper;
    }

}
