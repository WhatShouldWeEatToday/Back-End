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
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final FriendshipRepository friendshipRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        log.info("Received WebSocket command: {}", command);
        log.info("Native headers: {}", accessor.getNativeHeader("Authorization"));

        try {
            if (StompCommand.CONNECT.equals(command)) {
                log.info("WebSocket CONNECT request received");
                Member member = getMemberByAuthorizationHeader(
                        accessor.getFirstNativeHeader("Authorization"));
                String sessionId = accessor.getSessionId();

                log.info("User authenticated: loginId={}, nickname={}, sessionId={}", member.getLoginId(), member.getNickname(), sessionId);
                setSessionAttributes(accessor, member, sessionId);

            } else if (StompCommand.SUBSCRIBE.equals(command)) {
                String destination = accessor.getDestination();
                if (destination == null) {
                    log.error("Destination is null.");
                    throw new IllegalArgumentException("Destination cannot be null.");
                }

                if (destination.startsWith("/topic/public/")) {
                    String loginId = (String) getValue(accessor, "loginId");
                    String friendLoginId = extractPathSuffix(destination, "/topic/public/");
                    log.info("User subscribed: loginId = {}, friendLoginId = {}", loginId, friendLoginId);
                    setValue(accessor, "friendLoginId", friendLoginId);
                    validateMemberInFriendship(loginId, friendLoginId);

                } else if (destination.startsWith("/topic/votes/")) {
                    Long roomId = Long.valueOf(extractPathSuffix(destination, "/topic/votes/"));
                    setLongValue(accessor, "roomId", roomId);

                } else if (destination.startsWith("/topic/meet/")) {
                    Long roomId = Long.valueOf(extractPathSuffix(destination, "/topic/meet/"));
                    setLongValue(accessor, "roomId", roomId);

                } else if (destination.startsWith("/topic/departure/")) {
                    Long roomId = Long.valueOf(extractPathSuffix(destination, "/topic/departure/"));
                    setLongValue(accessor, "roomId", roomId);
                }
            } else if (StompCommand.DISCONNECT.equals(command)) {
                String loginId = (String) getValue(accessor, "loginId");
                log.info("WebSocket DISCONNECTED request received: loginId = {}", loginId);
            }

            log.info("header = {}", message.getHeaders());
            log.info("message = {}", message);
        } catch (Exception e) {
            log.error("Error during preSend: ", e);
            throw e;
        }

        return message;
    }

    private Member getMemberByAuthorizationHeader(String authHeaderValue) {
        log.info("Extracting member from authorization header");
        String accessToken = getTokenByAuthorizationHeader(authHeaderValue);

        Claims claims = jwtTokenProvider.getClaims(accessToken);
        String loginId = claims.get("sub", String.class);

        log.info("Member extracted: loginId={}", loginId);

        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException(loginId));
    }

    private String getTokenByAuthorizationHeader(String authHeaderValue) {
        if (Objects.isNull(authHeaderValue) || authHeaderValue.isBlank()) {
            throw new WebSocketException("authHeaderValue : " + authHeaderValue);
        }

        log.info("Validating JWT token");
        String accessToken = ExtractUtil.extractToken(authHeaderValue);
        jwtTokenProvider.validateToken(accessToken);

        return accessToken;
    }

    private String extractPathSuffix(String destination, String prefix) {
        return destination.startsWith(prefix) ? destination.substring(prefix.length()) : "";
    }

    private void validateMemberInFriendship(String memberLoginId, String friendLoginId) {
        friendshipRepository.findOneByMemberLoginIdAndFriendLoginId(memberLoginId, friendLoginId)
                .orElseThrow(() -> new WebSocketException("조회된 friendship 결과가 없습니다."));
    }

    private Object getValue(StompHeaderAccessor accessor, String key) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        Object value = sessionAttributes.get(key);

        if (Objects.isNull(value)) {
            throw new WebSocketException(key + "에 해당하는 값이 없습니다.");
        }
        return value;
    }

    private void setValue(StompHeaderAccessor accessor, String key, String value) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        sessionAttributes.put(key, value);
    }

    private void setLongValue(StompHeaderAccessor accessor, String key, Long value) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        sessionAttributes.put(key, value);
    }

    private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (Objects.isNull(sessionAttributes)) {
            throw new WebSocketException("SessionAttributes가 Null 입니다.");
        }
        return sessionAttributes;
    }

    private void setSessionAttributes(StompHeaderAccessor accessor, Member member, String sessionId) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        sessionAttributes.put("loginId", member.getLoginId());
        sessionAttributes.put("nickname", member.getNickname());
        sessionAttributes.put("sessionId", sessionId);
    }
}
