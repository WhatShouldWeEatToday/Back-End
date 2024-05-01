package kit.project.whatshouldweeattoday.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.domain.type.RoleType;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class JwtServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired JwtService jwtService  = new JwtServiceImpl(memberRepository);
    @Autowired EntityManager em;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String loginId_CLAIM = "loginId";
    private static final String BEARER = "Bearer";

    private String loginId = "loginId";

    @BeforeEach
    public void init() {
        Member member = Member.builder()
                .loginId(loginId)
                .loginPw("a12345678")
                .verifiedLoginPw("a12345678")
                .nickname("홍길동")
                .gender("Female")
                .age(24)
                .role(RoleType.USER)
                .build();

        memberRepository.save(member);
        clear();
    }

    private void clear() {
        em.flush();
        em.clear();
    }

    private DecodedJWT getVerify(String token) {
        return JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
    }

    @Test
    @DisplayName("AccessToken_발급")
    public void create_AccessToken(){
        // given, when
        String accessToken = jwtService.createAccessToken(loginId);

        DecodedJWT verify = getVerify(accessToken);

        String subject = verify.getSubject();
        String findByLoginId = verify.getClaim(loginId_CLAIM).asString();

        // then
        assertThat(findByLoginId).isEqualTo(loginId);
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }

    @Test
    @DisplayName("RefreshToken_발급")
    public void create_RefreshToken(){
        // given, when
        String refreshToken = jwtService.createRefreshToken();
        DecodedJWT verify = getVerify(refreshToken);
        String subject = verify.getSubject();
        String loginId = verify.getClaim(loginId_CLAIM).asString();

        // then
        assertThat(subject).isEqualTo(REFRESH_TOKEN_SUBJECT);
        assertThat(loginId).isNull(); // refreshToken 은 loginId가 없어야 함
    }

    @Test
    @DisplayName("RefreshToken_업데이트")
    public void update_RefreshToken() throws InterruptedException {
        // given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(loginId, refreshToken);
        clear();
        Thread.sleep(3000); // refreshToken 이 똑같이 발급되는 것을 방지

        // when
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(loginId, reIssuedRefreshToken);
        clear();

        // then
        assertThrows(Exception.class, () -> memberRepository.findByRefreshToken(refreshToken).get());
        assertThat(memberRepository.findByRefreshToken(reIssuedRefreshToken).get().getLoginId()).isEqualTo(loginId);
    }

    @Test
    @DisplayName("RefreshToken_제거")
    public void destroy_RefreshToken() {
        // given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(loginId, refreshToken);
        clear();

        // when
        jwtService.destroyRefreshToken(loginId);
        clear();

        // then
        assertThrows(Exception.class, () -> memberRepository.findByRefreshToken(refreshToken).get());

        Member member = memberRepository.findByLoginId(loginId).get();
        assertThat(member.getRefreshToken()).isNull();
    }

    @Test
    @DisplayName("AccessToken_헤더_설정")
    public void set_AccessToken_Header() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(loginId);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.setAccessTokenHeader(response, accessToken);

        // when
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        // then
        String headerAccessToken = response.getHeader(accessHeader);
        assertThat(headerAccessToken).isEqualTo(accessToken);
    }

    @Test
    @DisplayName("RefreshToken_헤더_설정")
    public void set_RefreshToken_Header() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(loginId);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.setRefreshTokenHeader(response, refreshToken);

        // when
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        // then
        String headerRefreshToken = response.getHeader(refreshHeader);
        assertThat(headerRefreshToken).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("토큰_전송")
    public void send_Token() {
        // given
        MockHttpServletResponse response = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(loginId);
        String refreshToken = jwtService.createRefreshToken();

        // when
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        // then
        String headerAccessToken = response.getHeader(accessHeader);
        String headerRefreshToken = response.getHeader(refreshHeader);

        assertThat(headerAccessToken).isEqualTo(accessToken);
        assertThat(headerRefreshToken).isEqualTo(refreshToken);
    }

    // 토큰을 Header 에 넣어 전송해주는 메서드
    private HttpServletRequest setRequest(String accessToken, String refreshToken) {
        MockHttpServletResponse response = new MockHttpServletResponse();
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        String headerAccessToken = response.getHeader(accessHeader);
        String headerRefreshToken = response.getHeader(refreshHeader);

        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addHeader(accessHeader, BEARER + headerAccessToken);
        request.addHeader(refreshHeader, BEARER + headerRefreshToken);

        return request;
    }

    @Test
    @DisplayName("AccessToken_추출")
    public void extractAccessToken() throws Exception {
        // given
        String accessToken = jwtService.createAccessToken(loginId);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest request = setRequest(accessToken, refreshToken);

        // when
        String extractAccessToken = jwtService.extractAccessToken(request).orElseThrow(()-> new Exception("토큰이 없습니다"));;

        // then
        assertThat(extractAccessToken).isEqualTo(accessToken);
        assertThat(getVerify(extractAccessToken).getClaim(loginId_CLAIM).asString()).isEqualTo(loginId);
    }

    @Test
    @DisplayName("RefreshToken_추출")
    public void extractRefreshToken() throws Exception {
        // given
        String accessToken = jwtService.createAccessToken(loginId);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest request = setRequest(accessToken, refreshToken);

        // when
        String extractRefreshToken = jwtService.extractRefreshToken(request).orElseThrow(()-> new Exception("토큰이 없습니다"));

        // then
        assertThat(extractRefreshToken).isEqualTo(refreshToken);
        assertThat(getVerify(extractRefreshToken).getSubject()).isEqualTo(REFRESH_TOKEN_SUBJECT);
    }

    @Test
    @DisplayName("loginId_추출")
    public void extractLoginId() throws Exception {
        // given
        String accessToken = jwtService.createAccessToken(loginId);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest request = setRequest(accessToken, refreshToken);

        String requestAccessToken = jwtService.extractAccessToken(request).orElseThrow(()-> new Exception("토큰이 없습니다"));

        // when
        String extractLoginId = jwtService.extractLoginId(requestAccessToken).orElseThrow(()-> new Exception("토큰이 없습니다"));

        // then
        assertThat(extractLoginId).isEqualTo(loginId);
    }

    @Test
    @DisplayName("토큰_유효성_검사")
    public void is_Token_Valid(){
        // given
        String accessToken = jwtService.createAccessToken(loginId);
        String refreshToken = jwtService.createRefreshToken();

        // when, then
        assertThat(jwtService.isTokenValid(accessToken)).isTrue();
        assertThat(jwtService.isTokenValid(refreshToken)).isTrue();
        assertThat(jwtService.isTokenValid(accessToken+"d")).isFalse();
        assertThat(jwtService.isTokenValid(accessToken+"d")).isFalse();
    }
}