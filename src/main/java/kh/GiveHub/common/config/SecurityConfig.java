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
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        // 정적 자원 접근을 permitAll()로
                        .requestMatchers(new AntPathRequestMatcher("/resources/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/img/**")).permitAll()

                        // 허용 페이지
                        .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/member/join")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/member/join.id")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/member/join.email")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/member/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/emailCheck")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/findIdemailCheck")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/findPwdemailCheck")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/member/findMyId")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/temporaryPwd")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/findmyidsuccess")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/findpassword")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/findmypasswordsuccess")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/page/PaymentPage")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/donation/donationlist")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/donation/donationdetail")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/news/newsList")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/news/newsDetail")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/donationlist")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/newsList")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/member/join-success")).permitAll()

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