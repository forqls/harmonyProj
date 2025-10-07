// kh.GiveHub.common.config.SecurityConfig.java

package kh.GiveHub.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        String[] permitAllUrls = {
                "/",
                "/member/login",
                "/member/join",
                "/member/join-success",
                "/member/findmyid",
                "/member/findmyidsuccess",
                "/member/findpassword",
                "/member/findpasswordsuccess",
                "/donation/donationlist",
                "/donation/donationdetail",
                "/news/newsList",
                "/news/newsDetail",
                "/page/PaymentPage"
        };

        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests

                        // 1. 관리자 페이지는 'ROLE_ADMIN' 권한만 접근 가능
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                        // 2. permitAll() 경로 허용
                        .requestMatchers(permitAllUrls).permitAll()

                        // 3. 그 외의 모든 요청은 '인증' 필요
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/member/login")
                        .loginProcessingUrl("/loginAction") // 로그인 폼의 action과 일치해야 합니다.
                        .failureUrl("/member/login?error")

                        // 4. customAuthenticationSuccessHandler Bean을 등록합니다.
                        .successHandler(customAuthenticationSuccessHandler())

                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

                // 1. 현재 인증된 사용자의 권한 목록을 가져옵니다.
                Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

                // 2. 권한 목록에 'ADMIN' 문자열이 포함된 권한
                boolean isAdmin = roles.stream()
                        .anyMatch(role -> role.toUpperCase().contains("ADMIN"));

                if (isAdmin) {
                    // 관리자 권한이 확인되면 관리자 메인 페이지로 이동
                    response.sendRedirect("/admin/main");
                } else {
                    // 그 외 (일반 사용자)는 메인 페이지로 이동
                    response.sendRedirect("/");
                }
            }
        };
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/css/**",
                "/js/**",
                "/img/**",
                "/upload/**",
                "/favicon.ico"
        );
    }
}