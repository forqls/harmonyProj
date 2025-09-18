package kh.GiveHub.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        // "/"와 "/resources/**"에 대한 모든 요청을 허용
                        // 정적 자원과 메인 페이지, 회원가입 관련 페이지에 대한 접근을 허용
                        .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/resources/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll() // CSS 폴더 허용
                        .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll() // JS 폴더 허용
                        .requestMatchers(new AntPathRequestMatcher("/img/**")).permitAll() // IMG 폴더 허용
                        .requestMatchers(new AntPathRequestMatcher("/member/join")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/member/join.id")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/emailCheck")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/findIdemailCheck")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/findPwdemailCheck")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/member/findMyId")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/temporaryPwd")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/findmyidsuccess")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/findpassword")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/findmypasswordsuccess")).permitAll()
                        // 이 외의 모든 요청은 인증 필요
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