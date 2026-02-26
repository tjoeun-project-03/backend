package com.jimline.global.security;

import java.util.Arrays;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.jimline.global.auth.JwtAuthenticationFilter;
import com.jimline.global.auth.JwtTokenProvider;

import jakarta.servlet.http.HttpServletResponse;
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
		.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)

        // 2. 세션을 사용하지 않으므로 STATELESS 설정
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    // 인증 실패 시 401을 반환하여 Flutter의 Refresh 로직을 깨움
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\": \"UNAUTHORIZED\", \"message\": \"인증이 필요합니다.\"}");
                })
            )
        // 3. 권한 설정
        .authorizeHttpRequests(auth -> auth
    		.requestMatchers(
    		        "/v3/api-docs/**",
    		        "/swagger-ui/**",
    		        "/swagger-ui.html"
    		    ).permitAll()
            .requestMatchers("/api/auth/**", "/error").permitAll()      // 회원가입, 로그인은 인증 없이 허용
            .requestMatchers("/api/admin/**").hasRole("ADMIN") // 관리자 API는 ADMIN 권한 필수
            .anyRequest().authenticated()                     // 그 외 모든 요청은 인증 필요
        )

        // 4. JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), 
                        UsernamePasswordAuthenticationFilter.class);

		return http.build();
    }
	
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. 허용할 Origin (프론트엔드 주소)
        configuration.addAllowedOrigin("http://localhost:3000"); // 리액트 기본 포트
        configuration.addAllowedOrigin("http://localhost:5173"); // Vite 기본 포트
        
        // 2. 허용할 HTTP Method
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // 3. 허용할 헤더
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "x-auth-token"));
        
        // 4. 자격 증명 허용 (Cookie 등 사용 시 필수)
        configuration.setAllowCredentials(true);
        
        // 5. 프리플라이트(Preflight) 캐싱 시간 (초 단위)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 적용
        return source;
    }
}

