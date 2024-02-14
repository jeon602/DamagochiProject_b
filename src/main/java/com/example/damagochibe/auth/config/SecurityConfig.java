package com.example.damagochibe.auth.config;

import com.example.damagochibe.auth.oauth.service.OauthService;
import com.example.damagochibe.auth.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final CustomUserDetailService customUserDetailService;
    private final TokenExceptionFilter tokenExceptionFilter;
    private final TokenEntryPoint tokenEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // csrf disable
                .csrf(csrf->csrf.disable())
                .exceptionHandling(exceptionHandling->exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(tokenEntryPoint)
                )
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers("/auth/login","/auth/isSocialMember","/auth/logout","/battle/**","/ws/**").permitAll()
                        .requestMatchers("/auth/accessToken").authenticated()
                        .requestMatchers("/auth/**").hasAnyAuthority("USER")
                        .anyRequest().permitAll()
                )
                .logout((LogoutConfigurer::permitAll))
                .sessionManagement(sessionManagement->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());
        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());
        http.cors(cors -> cors.configurationSource(corsConfigurationSource())); // CORS 설정 적용
        http.addFilterBefore(new TokenAuthenticationFilter(tokenProvider, customUserDetailService),
                UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(tokenExceptionFilter, TokenAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // 모든 출처 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // 허용할 HTTP 메소드
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
//        configuration.setAllowCredentials(true); // 쿠키 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 적용
        return source;
    }
}



//웹 보안을 설정하는 클래스임
// 인증과 권한 부여를 관리하고 CORS (Cross-Origin Resource Sharing)를 구성하여
// 클라이언트와의 HTTP 요청 간에 보안 정책을 관리함
// 시큐리티필터체인 : 빈 설정 필터체인 메소드가 시큪리티 필터 체인 빈을 생성함
// 이는 http 보안 필터 테인을 구성함, 스프링 시큐리티에서 http 요청을 처리하는 핵심 역할을 함
//
// http 보안 설정
//CORS 구성:
//.cors()를 통해 CORS (Cross-Origin Resource Sharing) 구성을 적용합니다.
//corsConfigurationSource() 메서드를 통해 CORS 구성을 설정합니다.
//모든 출처, 메서드, 헤더를 허용하도록 구성됩니다.

// 패스워드 인코더 빈 설정 빈을 등록해 패스워드 인코딩을 처리함
// 인증 필터 추가해 커스텀 인증 필터를 등록함
// 해당 필터는 토큰  기반의 인증을 처리함
