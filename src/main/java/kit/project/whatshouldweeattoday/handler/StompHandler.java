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

    public static final String DEFAULT_PATH = "/topic/public/";

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final FriendshipRepository friendshipRepository;

    // Websocket 을 통해 들어온 요청이 처리 되기 전 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if(StompCommand.CONNECT.equals(command)) { // Websocket 연결 요청 -> JWT 인증
            // JWT 인증
            Member member = getMemberByAuthorizationHeader(
                    accessor.getFirstNativeHeader("Authorization"));

            // 인증 후 데이터를 헤더에 추가
            setValue(accessor, "loginId", member.getLoginId());
            setValue(accessor, "nickname", member.getNickname());

        } else if (StompCommand.SUBSCRIBE.equals(command)) {
            String loginId = (String) getValue(accessor, "loginId");
            String friendLoginId = parseFriendLoginIdFromPath(accessor);
            log.info("loginId = {}, friendLoginId = {}", loginId, friendLoginId);
            setValue(accessor, "friendLoginId", friendLoginId);
            validateMemberInFriendship(loginId, friendLoginId);

        } else if(StompCommand.DISCONNECT == command) { // Websocket 연결 종료
            Long loginId = (Long) getValue(accessor, "loginId");
            log.info("DISCONNECTED loginId = {}", loginId);
        }

        log.info("header = {}", message.getHeaders());
        log.info("message = {}", message);

        return message;
    }

    private Member getMemberByAuthorizationHeader(String authHeaderValue) {
        String accessToken = getTokenByAuthorizationHeader(authHeaderValue);

        Claims claims = (Claims) jwtTokenProvider.getAuthentication(accessToken);
        String loginId = claims.get("loginId", String.class);

        return memberRepository.findByLoginId(loginId).orElseThrow(() -> new UsernameNotFoundException(loginId));
    }

    private String getTokenByAuthorizationHeader(String authHeaderValue) {
        if(Objects.isNull(authHeaderValue) || authHeaderValue.isBlank()) {
            throw new WebSocketException("authHeaderValue : " + authHeaderValue);
        }

        String accessToken = ExtractUtil.extractToken(authHeaderValue);
        jwtTokenProvider.validateToken(accessToken); // 예외 발생 가능

        return accessToken;
    }

    private String parseFriendLoginIdFromPath(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        return destination.substring(DEFAULT_PATH.length());
    }

    private void validateMemberInFriendship(String memberLoginId, String friendLoginId) {
        friendshipRepository.findOneByMemberLoginIdAndFriendLoginId(memberLoginId, friendLoginId)
                .orElseThrow(() -> new WebSocketException("조회된 friendship 결과가 없습니다."));
    }

    private Object getValue(StompHeaderAccessor accessor, String key) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        Object value = sessionAttributes.get(key);

        if(Objects.isNull(value)) {
            throw new WebSocketException(key + "에 해당하는 값이 없습니다.");
        }
        return value;
    }

    private void setValue(StompHeaderAccessor accessor, String key, String value) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        sessionAttributes.put(key, value);
    }

    private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if(Objects.isNull(sessionAttributes)) {
            throw new WebSocketException("SessionAttributes가 Null 입니다.");
        }
        return sessionAttributes;
    }


}
