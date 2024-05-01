package kit.project.whatshouldweeattoday.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import kit.project.whatshouldweeattoday.security.filter.JsonUsernamePasswordAuthenticationFilter;
import kit.project.whatshouldweeattoday.security.filter.JwtAuthenticationProcessingFilter;
import kit.project.whatshouldweeattoday.security.handler.LoginFailureHandler;
import kit.project.whatshouldweeattoday.security.handler.LoginSuccessJWTProvideHandler;
import kit.project.whatshouldweeattoday.security.service.JwtService;
import kit.project.whatshouldweeattoday.security.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginService loginService;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    // 특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http    .formLogin(AbstractHttpConfigurer::disable) // formLogin 인증방법 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // httpBasic 인증방법 비활성화(특정 리소스에 접근할 때 loginId 와 loginPw 물어봄)
                .csrf(AbstractHttpConfigurer::disable) // 정상적인 사용자가 의도치 않은 위조 요청을 보내는 것
//                .authorizeHttpRequests((authorize) -> authorize // 인증, 인가 필요한 URL 지정
//                        .requestMatchers("/", "/signup", "/signin","/api/signup", "/api/signin", "/confirmId", "/confirmNickname").permitAll() // 지정된 URL 은 인증, 인가 없이도 접근 허용
//                        .anyRequest().authenticated())
                .logout((logout) -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)) // 로그아웃 이후 전체 세션 삭제
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        http    .addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class) // 커스터마이징 된 필터를 SpringSecurityFilterChain 에 등록
                .addFilterAfter(jwtAuthenticationProcessingFilter(), JsonUsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(loginService);

        return new ProviderManager(provider);
    }

    @Bean
    public LoginSuccessJWTProvideHandler loginSuccessJWTProvideHandler(){
        return new LoginSuccessJWTProvideHandler(jwtService, memberRepository);//변경
    }

    @Bean
    public LoginFailureHandler loginFailureHandler(){
        return new LoginFailureHandler();
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter(){
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);

        jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());

        jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessJWTProvideHandler());

        jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());//변경
        return jsonUsernamePasswordLoginFilter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter(){
        return new JwtAuthenticationProcessingFilter(jwtService, memberRepository);
    }
}
