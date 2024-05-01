package kit.project.whatshouldweeattoday.config;

import kit.project.whatshouldweeattoday.security.JwtTokenProvider;
import kit.project.whatshouldweeattoday.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("OPTIONS", "GET", "POST", "PATCH", "DELETE"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .formLogin(AbstractHttpConfigurer::disable) // formLogin 인증방법 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // httpBasic 인증방법 비활성화(특정 리소스에 접근할 때 loginId 와 loginPw 물어봄)
                .csrf(AbstractHttpConfigurer::disable) // 정상적인 사용자가 의도치 않은 위조 요청을 보내는 것
                .authorizeHttpRequests((authorize) -> authorize // 인증, 인가 필요한 URL 지정
                        .requestMatchers(HttpMethod.OPTIONS, "/api/signin").permitAll()
//                        .requestMatchers("/", "/signup", "/signin","/api/signup", "/api/signin", "/confirmId", "/confirmNickname").permitAll() // 지정된 URL 은 인증, 인가 없이도 접근 허용
                        .anyRequest().permitAll())
                .logout((logout) -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)) // 로그아웃 이후 전체 세션 삭제
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        httpSecurity .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
