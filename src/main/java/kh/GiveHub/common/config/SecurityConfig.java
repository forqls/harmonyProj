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
                "/resources/**",
                "/css/**",
                "/js/**",
                "/img/**",

                // 메인 및 회원 관련
                "/",
                "/member/join",
                "/member/join.id",
                "/member/join.email",
                "/member/login",
                "/member/join-success",

                // 이메일 및 비밀번호 찾기 관련
                "/emailCheck",
                "/findIdemailCheck",
                "/findPwdemailCheck",
                "/member/findMyId",
                "/member/findpassword",
                "/temporaryPwd",
                "/findmyidsuccess",
                "/findmypasswordsuccess",

                // 기타 목록 및 페이지
                "/page/PaymentPage",
                "/donation/donationlist",
                "/donation/donationdetail",
                "/news/newsList",
                "/news/newsDetail",
                "/donationlist",
                "/newsList"
        };

        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(permitAllUrls).permitAll()

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