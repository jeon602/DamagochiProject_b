package com.example.damagochibe.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AppConfig {
    @Value("${aws.accessKeyId}")
    private String accessKeyId;
    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        AwsCredentialsProvider provider = StaticCredentialsProvider.create(credentials);
        S3Client s3= S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(provider)
                .build();

        return s3;
    }

}

//AWS S3 서비스를 사용할수 있는 클라이언트 생성 설정 클래스임.

// aws s3클라이언트를 생성하기 위한 설정 클래스로, @Configuration 어노테이션을 사용해 설정 클래스임을 나타냄,.
// @value를 사용해 외부 설정 파일에서 aws 액세스 키 id와 비밀 액세스 키를 가져옴.
// 직접 키를 포함시키지 않고 외부 설정 파일에 따라 변경할 수 있음

// @Bean 어노테이션을 상ㅇ해 s3Client()메소드를 빈으로 정의함
//aws키를 사용해 s3Client 인스턴스를 생성하고 구성함.

//  S3Client.builder()를 사용하여 S3 클라이언트를 생성하고,
//  .region() 메서드를 사용하여 지역(Region)을 설정하고,
//  .credentialsProvider() 메서드를 사용하여 인증 자격 증명 공급자를 설정함.
//  AWS 액세스 키를 직접 제공하는 방식으로
//  StaticCredentialsProvider를 사용함.
//  .build() 메서드를 호출하여 S3 클라이언트를 구성하고 반환함.