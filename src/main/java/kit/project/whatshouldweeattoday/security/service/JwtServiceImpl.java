package kit.project.whatshouldweeattoday.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
@Slf4j
public class JwtServiceImpl implements JwtService {

    /* 1 */
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration}")
    private long accessTokenValidityInSeconds;
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidityInSeconds;

    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    /* 2 */
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String loginId_CLAIM = "loginId";
    private static final String BEARER = "Bearer";

    private final MemberRepository memberRepository;

    /*
     * 3. JWT 토큰 생성
     */
    @Override
    public String createAccessToken(String loginId) {
        // JWT 토큰 생성하는 빌더 반환
        return JWT.create()
                // 빌더를 통해 JWT 의 Subject 를 정함
                // AccessToken 이므로 위에서 설정했던 ACCESS_TOKEN_SUBJECT 를 함
                .withSubject(ACCESS_TOKEN_SUBJECT)
                // 만료시간 설정
                // 80 * 1000 이므로 80초 이후 토큰 만료
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds * 1000))
                // 클레임으로 loginId 하나만 사용
                // 추가적으로 식별자/이름 등의 정보 추가 가능 -> .withClaim(클래임 이름, 클래임 값)으로 설정
                .withClaim(loginId_CLAIM, loginId)
                // HMAC512 알고리즘을 사용해 지정한 secret 키로 암호화
                .sign(Algorithm.HMAC512(secret));
    }

    /*
     * JWT 토큰 생성
     */
    @Override
    public String createRefreshToken() {
        return JWT.create()
                // 빌더를 통해 JWT 의 Subject 를 정함
                // RefreshToken 이므로 위에서 설정했던 REFRESH_TOKEN_SUBJECT 를 함
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds * 1000))
                .sign(Algorithm.HMAC512(secret));
    }

    @Override
    public void updateRefreshToken(String loginId, String refreshToken) {
        memberRepository.findByLoginId(loginId)
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken),
                        () -> new Exception("회원 조회 실패")
                );
    }

    @Override
    public void destroyRefreshToken(String loginId) {
        memberRepository.findByLoginId(loginId)
                .ifPresentOrElse(
                        Member::destroyRefreshToken,
                        () -> new BadRequestException("회원 조회 실패")
                );
    }

    @Override
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
        tokenMap.put(REFRESH_TOKEN_SUBJECT, refreshToken);
    }

    /* 5 */
    @Override
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
    }

    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader)).filter(
                accessToken -> accessToken.startsWith(BEARER)
        ).map(accessToken -> accessToken.replace(BEARER, ""));
    }

    @Override
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader)).filter(
                refreshToken -> refreshToken.startsWith(BEARER)
        ).map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    /*
     * 4. 토큰에서 User 정보 추출
     */
    @Override
    public Optional<String> extractLoginId(String accessToken) {
        try {
            return Optional.ofNullable(
                    // 토큰의 서명 유효성을 검사하는데 사용할 알고리즘이 있는 JWT verifier builder 를 반환
                    JWT.require(Algorithm.HMAC512(secret))
                            // 반환된 builder 로 JWT verifier 생성
                            .build()
                            // accessToken 을 검증하고 유효하지 않다면 예회 발생
                            .verify(accessToken)
                            // claim 을 가져옴
                            .getClaim(loginId_CLAIM)
                            .asString()
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    @Override
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 Token입니다", e.getMessage());
            return false;
        }
    }
}
