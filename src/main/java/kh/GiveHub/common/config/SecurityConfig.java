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

    //정적 자원에 대해서 보안 필터 체인을 완전히 무시
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                // 정적 자원 경로 명시 resources 폴더 하위의 모든 정적 자원 포함
                .requestMatchers("/resources/**", "/css/**", "/js/**", "/img/**", "/h2-console/**")
                .requestMatchers(new AntPathRequestMatcher("/resources/**")) // 혹시 몰라 AntPath도 남깁니다.
                .requestMatchers(new AntPathRequestMatcher("/css/**"))
                .requestMatchers(new AntPathRequestMatcher("/js/**"))
                .requestMatchers(new AntPathRequestMatcher("/img/**"));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //모든 permitAll() 설정 옮기기, 정적 자원 설정은 제거
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        // 메인 페이지와 회원가입/로그인/비밀번호 찾기 관련 모든 요청을 허용
                        .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/member/join")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/member/join.id")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/member/join.email")).permitAll() // 이메일 체크도 추가
                        .requestMatchers(new AntPathRequestMatcher("/member/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/emailCheck")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/findIdemailCheck")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/findPwdemailCheck")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/member/findMyId")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/temporaryPwd")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/findmyidsuccess")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/findpassword")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/findmypasswordsuccess")).permitAll()

                        // PaymentPage는 결제 관련 임시 허용
                        .requestMatchers(new AntPathRequestMatcher("/page/PaymentPage")).permitAll()

                        // 이 외의 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // login() 설정을 추가 -> Spring Security가 로그인 처리
                .formLogin(formLogin -> formLogin.disable())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}