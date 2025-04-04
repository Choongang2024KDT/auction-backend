package com.choongang.auction.streamingauction.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//전역 크로스오리진 설정 : 허용할 클라이언트 설정
@Configuration
public class CrossOriginConfig implements WebMvcConfigurer {

    private String[] urls = {
            "http://localhost:3000",
            "http://localhost:5173",
            "http://localhost:5174",
            "http://goose123.shop.s3-website.ap-northeast-2.amazonaws.com",
            "https://goose123.shop"
    };

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**") //클라이언트의 어떤 요청방식을 허용할지
                .allowedOrigins(urls) //어떤 클라이언트의 주소를 허용할지
                .allowedMethods("*")  //어떤 요청방식을 허용할지
                .allowedHeaders("*") //어떤 헤더를 포함시킬지
                .allowCredentials(true) //쿠키허용
        ;
    }
}