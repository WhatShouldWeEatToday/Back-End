package kit.project.whatshouldweeattoday.handler;

import io.jsonwebtoken.Claims;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.exception.WebSocketException;
import kit.project.whatshouldweeattoday.repository.FriendshipRepository;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import kit.project.whatshouldweeattoday.security.service.JwtTokenProvider;
import kit.project.whatshouldweeattoday.security.util.ExtractUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    // WebSocket 메시지의 핸들링 담당, ChannelInterceptor 인터페이스를 구현하여 메시지가 채널을 통해 전송되기 전에 가로채고, 필요한 처리를 수행함
    public static final String DEFAULT_PATH = "/topic/public/";

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final FriendshipRepository friendshipRepository;

    /**
     * 메시지가 채널에 보내지기 전에 호출됨
     * @param message
     * @param channel
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        log.info("Received WebSocket command: {}", command);

        /**
         * CONNECT
         * 클라이언트가 WebSocket 연결을 시도할 때 호출됨
         * Authorization 헤더에서 JWT 토큰을 추출하고, 이를 통해 사용자 정보를 조회함
         * 조회한 사용자 정보 (loginId, nickname)를 세션 속성에 저장함
         */
        if(StompCommand.CONNECT.equals(command)) {
            log.info("WebSocket CONNECT request received");

            Member member = getMemberByAuthorizationHeader(
                    accessor.getFirstNativeHeader("Authorization"));

            log.info("User authenticated: loginId={}, nickname={}", member.getLoginId(), member.getNickname());

            setValue(accessor, "loginId", member.getLoginId());
            setValue(accessor, "nickname", member.getNickname());
        }
        /**
         * SUBSCRIBE
         * 클라이언트가 특정 채팅방을 구독할 때 호출됨
         * 세션 속성에서 loginId를 가져오고, 구독하려는 채널 경로에서 친구의 loginId를 추출함
         * 사용자와 친구 관계를 검증
         */
        else if (StompCommand.SUBSCRIBE.equals(command)) {
            String loginId = (String) getValue(accessor, "loginId");
            String friendLoginId = parseFriendLoginIdFromPath(accessor);
            log.info("User subscribed: loginId = {}, friendLoginId = {}", loginId, friendLoginId);
            setValue(accessor, "friendLoginId", friendLoginId);
            validateMemberInFriendship(loginId, friendLoginId);

        }
        /**
         * DISCONNECT
         * 클라이언트가 WebSocket 연결을 해제할 때 호출됨
         * 세션 속성에서 loginId를 가져와 로그를 남김
         */
        else if(StompCommand.DISCONNECT == command) {
            Long loginId = (Long) getValue(accessor, "loginId");
            log.info("WebSocket DISCONNECTED request received: loginId = {}", loginId);
        }

        log.info("header = {}", message.getHeaders());
        log.info("message = {}", message);

        return message;
    }

    /**
     * Authorization 헤더에서 JWT 토큰을 추출하고, 이를 통해 사용자 정보를 조회함
     * @param authHeaderValue
     */
    private Member getMemberByAuthorizationHeader(String authHeaderValue) {
        log.info("Extracting member from authorization header");
        String accessToken = getTokenByAuthorizationHeader(authHeaderValue);

        Claims claims = (Claims) jwtTokenProvider.getAuthentication(accessToken);
        String loginId = claims.get("loginId", String.class);

        log.info("Member extracted: loginId={}", loginId);

        return memberRepository.findByLoginId(loginId).orElseThrow(() -> new UsernameNotFoundException(loginId));
    }

    /**
     * Authorization 헤더에서 JWT 토큰을 추출하고 유효성을 검증하는 메서드
     * @param authHeaderValue
     */
    private String getTokenByAuthorizationHeader(String authHeaderValue) {
        if(Objects.isNull(authHeaderValue) || authHeaderValue.isBlank()) {
            throw new WebSocketException("authHeaderValue : " + authHeaderValue);
        }

        log.info("Validating JWT token");
        String accessToken = ExtractUtil.extractToken(authHeaderValue);
        jwtTokenProvider.validateToken(accessToken); // 예외 발생 가능

        return accessToken;
    }

    /**
     * 구독 경로에서 친구의 loginId를 추출하는 메서드
     * @param accessor
     */
    private String parseFriendLoginIdFromPath(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        return destination.substring(DEFAULT_PATH.length());
    }

    /**
     * 사용자와 친구 관계를 검증하는 메서드
     * @param memberLoginId
     * @param friendLoginId
     */
    private void validateMemberInFriendship(String memberLoginId, String friendLoginId) {
        friendshipRepository.findOneByMemberLoginIdAndFriendLoginId(memberLoginId, friendLoginId)
                .orElseThrow(() -> new WebSocketException("조회된 friendship 결과가 없습니다."));
    }

    /**
     * 세션 속성에서 값을 가져오는 메서드
     * @param accessor
     * @param key
     */
    private Object getValue(StompHeaderAccessor accessor, String key) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        Object value = sessionAttributes.get(key);

        if(Objects.isNull(value)) {
            throw new WebSocketException(key + "에 해당하는 값이 없습니다.");
        }
        return value;
    }

    /**
     * 세션 속성에 값을 설정하는 메서드
     * @param accessor
     * @param key
     * @param value
     */
    private void setValue(StompHeaderAccessor accessor, String key, String value) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        sessionAttributes.put(key, value);
    }

    /**
     * 세션 속성을 가져오는 메서드
     * @param accessor
     */
    private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if(Objects.isNull(sessionAttributes)) {
            throw new WebSocketException("SessionAttributes가 Null 입니다.");
        }
        return sessionAttributes;
    }
}