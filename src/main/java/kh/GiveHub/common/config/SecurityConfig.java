// kh.GiveHub.common.config.SecurityConfig.java

package kh.GiveHub.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer; // <<< 이 import를 추가합니다.
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        String[] permitAllUrls = {
                // ... 정적 자원 및 목록 페이지
        };

        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests

                        // 관리자 페이지는 '로그인만' 요구
                        .requestMatchers("/admin/**").authenticated()

                        // permitAll() 경로 허용
                        .requestMatchers(permitAllUrls).permitAll()

                        // 그 외의 모든 요청 (주로 로그인/회원가입 외 나머지)은 '인증' 필요
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin.disable())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}