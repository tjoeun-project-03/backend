package com.jimline.global.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); // 비밀번호 암호화 도구
	}

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
        // 1. CSRF, Form Login, Http Basic 모두 비활성화 (람다 방식)
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)

        // 2. 세션을 사용하지 않으므로 STATELESS 설정
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        // 3. 권한 설정
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()      // 회원가입, 로그인은 인증 없이 허용
            .requestMatchers("/api/admin/**").hasRole("ADMIN") // 관리자 API는 ADMIN 권한 필수
            .requestMatchers("/api/auth/**", "/error").permitAll()
            .anyRequest().authenticated()                     // 그 외 모든 요청은 인증 필요
        )

        // 4. JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), 
                        UsernamePasswordAuthenticationFilter.class);

		return http.build();
    }
}