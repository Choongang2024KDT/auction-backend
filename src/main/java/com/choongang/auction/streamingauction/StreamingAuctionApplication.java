package com.choongang.auction.streamingauction;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StreamingAuctionApplication {

    public static void main(String[] args) {
//        // .env 파일 로드
//        Dotenv dotenv = Dotenv.load();
//
//        // .env 파일에서 읽은 값을 System.setProperty로 Spring의 환경에 추가
//        System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
//        System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
//        System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
//        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
//        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
//        System.setProperty("AWS_ACCESS_KEY", dotenv.get("AWS_ACCESS_KEY"));
//        System.setProperty("AWS_SECRET_KEY", dotenv.get("AWS_SECRET_KEY"));
//        System.setProperty("AWS_S3_BUCKET", dotenv.get("AWS_S3_BUCKET"));
//        System.setProperty("JWT_SECRET_KEY", dotenv.get("JWT_SECRET_KEY"));
        // .env 파일 로드
//        Dotenv dotenv = Dotenv.load();
//
//        // .env 파일에서 읽은 값을 System.setProperty로 Spring의 환경에 추가
//        System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
//        System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
//        System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
//        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
//        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
//        System.setProperty("AWS_ACCESS_KEY", dotenv.get("AWS_ACCESS_KEY"));
//        System.setProperty("AWS_SECRET_KEY", dotenv.get("AWS_SECRET_KEY"));
//        System.setProperty("AWS_S3_BUCKET", dotenv.get("AWS_S3_BUCKET"));
//        System.setProperty("JWT_SECRET_KEY", dotenv.get("JWT_SECRET_KEY"));

        SpringApplication.run(StreamingAuctionApplication.class, args);
    }
}
