// kh.GiveHub.common.config.SecurityConfig.java

package kh.GiveHub.common.config;

import jakarta.servlet.http.HttpSession;
import kh.GiveHub.member.model.service.MemberService;
import kh.GiveHub.member.model.vo.Member;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private MemberService mService;

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
                        .loginProcessingUrl("/member/login")
                        .usernameParameter("memId")
                        .passwordParameter("memPwd")
                        .failureUrl("/member/login?error")

                        // 4. customAuthenticationSuccessHandler Bean을 등록
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

                // 1. 인증된 사용자의 ID(username)를 가져옵니다. (Spring Security의 Principal)
                String memberId = authentication.getName();

                // 2. MemberService를 사용하여 전체 Member 객체를 DB에서 다시 가져옵니다.
                Member memberSearch = new Member();
                memberSearch.setMemId(memberId);
                Member loginUser = mService.login(memberSearch); // 기존 login 쿼리 재사용

                // 🌟 CRITICAL FIX: HttpSession에 loginUser 객체를 수동으로 설정합니다. 🌟
                HttpSession session = request.getSession(true);
                session.setAttribute("loginUser", loginUser);

                // 3. 권한 목록을 가져와 로그 출력
                Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
                System.out.println("로그인 성공! 현재 사용자에게 부여된 권한: " + roles);

                // 4. 리다이렉션 로직 (원래 로직과 동일)
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