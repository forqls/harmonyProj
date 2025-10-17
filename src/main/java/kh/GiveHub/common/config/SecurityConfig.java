package kh.GiveHub.common.config;

import jakarta.servlet.http.HttpSession;
import kh.GiveHub.member.model.service.MemberService;
import kh.GiveHub.member.model.vo.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

    @Autowired
    private MemberService mService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        String[] staticResources = {
                "/css/**", "/js/**", "/img/**", "/upload/**", "/favicon.ico"
        };

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
                // CSRF 비활성화 (기존과 동일)
                .csrf(csrf -> csrf.disable())

                // 헤더 설정 (CSP 추가)
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "img-src 'self' data: https://pub-d307c9789e8a4ec2b24b351bfb46478e.r2.dev; " +
                                                "script-src 'self' https://code.jquery.com 'unsafe-inline';"
                                )
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(staticResources).permitAll() // static 리소스 허용
                        .requestMatchers(permitAllUrls).permitAll()   // 기존 permitAll URL 허용
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") // 관리자 페이지
                        .anyRequest().authenticated()                 // 그 외 모든 요청은 인증 필요
                )

                .formLogin(formLogin -> formLogin
                        .loginPage("/member/login")
                        .loginProcessingUrl("/member/login")
                        .usernameParameter("memId")
                        .passwordParameter("memPwd")
                        .failureUrl("/member/login?error")
                        .successHandler(customAuthenticationSuccessHandler())
                        .permitAll()
                );

        return http.build();
    }


    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
                String memberId = authentication.getName();
                Member memberSearch = new Member();
                memberSearch.setMemId(memberId);
                Member loginUser = mService.login(memberSearch);
                HttpSession session = request.getSession(true);
                session.setAttribute("loginUser", loginUser);
                Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
                System.out.println("로그인 성공! 현재 사용자에게 부여된 권한: " + roles);

                if (roles.contains("ROLE_ADMIN")) {
                    response.sendRedirect("/admin/main");
                } else {
                    response.sendRedirect("/");
                }
            }
        };
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}