package com.example.damagochibe.auth.config;

import com.example.damagochibe.auth.repository.RefreshTokenRepository;
import com.example.damagochibe.member.entity.Member;
import com.example.damagochibe.member.repository.MemberRepository;
import com.example.damagochibe.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthConfig {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    public Member tokenValidationService(HttpServletRequest httpServletRequest) {
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Incorrect or missing Authorization header");
        }
        String token = authorizationHeader.substring(7);
        Long memberIdByAccessToken = refreshTokenRepository.findMemberIdByAccessToken(token);
        Optional<Member> byMemberId = memberRepository.findByMemberId(memberIdByAccessToken);
        return byMemberId.orElse(null);
    }
    public Member tokenValidationServiceV1(String token) {
        Long memberIdByAccessToken = refreshTokenRepository.findMemberIdByAccessToken(token);
        Optional<Member> byMemberId = memberRepository.findByMemberId(memberIdByAccessToken);
        return byMemberId.orElse(null);
    }
}
// 인증 관련 구성을 담당하는 클래스임 : Token을 사용해 인증된 사용자를 식별하는데 사용함
// 의존성 주입 멤버리포지토리와 리프레쉬 토큰리포지토리가  AuthConfig클래스의 생성자를 통해 주입됨 이를 통해 해당 클래스에서 DB와
//상호작용해 사용자와 토큰을 관리할 수 있음
//tokenValidationService 메서드
//HttpServletRequest를 인자로 받아 헤더에서 Authotization을 읽어와 토큰을 추출함
//추출한 토큰으로 멤버 아이디를 찾는다 멤버 아이디를 사용해 해당 멤버를 조회함
// 추출한 토큰으로 멤버 아이디를 찾고, 멤버아이디로 멤버를 조회함
// 존재하지 않으면 null값을 반환
//CorsConfigurationSource 인터페이스를 구현하는 빈을 정의하지 않았음 일반적으로 설정을 위한 구성을 담당하는 클래스에서 이를 구현하고 빈으로 등록함
// 현재 코드에서는 CORS구성이 누락되어 있음 (20240214 기준.)

//이 클래스의 목적 :
// 사용자의 토큰을 검증헤 인증된 사용자를 식별하는데 사용됨  사용자가 데이터베이스에 존재하는 지 확인하는 역할을 함




