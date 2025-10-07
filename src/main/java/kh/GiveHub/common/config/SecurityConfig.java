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
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 정적 리소스(css, js, img 등)는 아예 보안 검사를 무시하도록 설정
        return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**", "/img/**", "/upload/**");//나중에 "/favicon.ico", 추가하기
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        String[] permitAllUrls = {
                // 핵심 페이지
                "/",
                "/member/login",
                "/member/join",
                "/member/join-success",

                // 멤버 관련
                "/member/findMyId",
                "/member/findMyIdSuccess",
                "/member/findPassword",
                "/member/findPasswordSuccess",

                // 목록 및 상세 페이지 (비로그인 접근 허용)
                "/donation/donationList",
                "/donation/donationDetail",
                "/news/newsList",
                "/news/newsDetail",

                // 결제 페이지
                "/page/PaymentPage"
        };

        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests

                        // permitAll() 경로 허용
                        .requestMatchers(permitAllUrls).permitAll()

                        // 관리자 페이지는 '로그인만' 요구 (인증된 사용자)
                        .requestMatchers("/admin/**").authenticated()

                        // 그 외의 모든 요청은 '인증' 필요
                        .anyRequest().authenticated()
                )
                // formLogin()을 비활성화(.disable()) 하면 안됨
                // 로그인 페이지를 명시하고 permitAll()로 접근을 허용
                .formLogin(formLogin -> formLogin
                        .loginPage("/member/login") // 로그인 페이지 URL
                        .defaultSuccessUrl("/") // 로그인 성공 시 이동할 URL
                        .failureUrl("/member/login?error") // 로그인 실패 시 URL
                        .permitAll() // 로그인 페이지에 대한 접근은 모두 허용
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}