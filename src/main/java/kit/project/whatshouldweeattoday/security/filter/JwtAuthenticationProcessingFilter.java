package kit.project.whatshouldweeattoday.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import kit.project.whatshouldweeattoday.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    private final String NO_CHECK_URL = "/api/signin"; // 1

    /**
     * 1. 리프레시 토큰이 오는 경우 -> 유효하면 AccessToken 재발급후, 필터 진행 X, 바로 튕기기
     *
     * 2. 리프레시 토큰은 없고 AccessToken만 있는 경우 -> 유저정보 저장후 필터 계속 진행
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return; //안해주면 아래로 내려가서 계속 필터를 진행해버림
        }
        String refreshToken = jwtService.extractRefreshToken(request).filter(jwtService::isTokenValid).orElse(null);

        if(refreshToken != null){
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return;
        }
        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        jwtService.extractAccessToken(request).filter(jwtService::isTokenValid)
                .flatMap(jwtService::extractLoginId)
                .flatMap(memberRepository::findByLoginId)
                .ifPresent(this::saveAuthentication);
//        jwtService.extractAccessToken(request).filter(jwtService::isTokenValid).ifPresent(
//                accessToken -> jwtService.extractloginId(accessToken).ifPresent(
//                        loginId -> userRepository.findByloginId(loginId).ifPresent(
//                                user -> saveAuthentication(user)
//                        )
//                )
//        );
        filterChain.doFilter(request,response);
    }



    private void saveAuthentication(Member member) {
        UserDetails user = User.builder()
                .username(member.getLoginId())
                .password(member.getLoginPw())
                .roles(member.getRole().name())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,authoritiesMapper.mapAuthorities(user.getAuthorities()));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        memberRepository.findByRefreshToken(refreshToken).ifPresent(
                member -> jwtService.sendAccessToken(response, jwtService.createAccessToken(member.getLoginId()))
        );
    }
}