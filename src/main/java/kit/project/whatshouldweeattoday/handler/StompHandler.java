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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    public static final String DEFAULT_PATH = "/topic/public/";
    public static final String DEFAULT_PATH_NO_TRAILING_SLASH = "/topic/public";

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final FriendshipRepository friendshipRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        log.info("Received WebSocket command: {}", command);

        try {
            if (StompCommand.CONNECT.equals(command)) {
                log.info("WebSocket CONNECT request received");
                Member member = getMemberByAuthorizationHeader(
                        accessor.getFirstNativeHeader("Authorization"));
                log.info("User authenticated: loginId={}, nickname={}", member.getLoginId(), member.getNickname());
                setValue(accessor, "loginId", member.getLoginId());
                setValue(accessor, "nickname", member.getNickname());

            } else if (StompCommand.SUBSCRIBE.equals(command)) {
                String loginId = (String) getValue(accessor, "loginId");
                String friendLoginId = parseFriendLoginIdFromPath(accessor);
                log.info("User subscribed: loginId = {}, friendLoginId = {}", loginId, friendLoginId);
                setValue(accessor, "friendLoginId", friendLoginId);
                validateMemberInFriendship(loginId, friendLoginId);

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

    private String parseFriendLoginIdFromPath(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();

        if (destination == null) {
            log.error("Destination is null.");
            throw new IllegalArgumentException("Destination cannot be null.");
        }

        if (!destination.startsWith(DEFAULT_PATH) && !destination.equals(DEFAULT_PATH_NO_TRAILING_SLASH)) {
            log.error("Destination does not start with the default path: " + destination);
            throw new IllegalArgumentException("Invalid destination path.");
        }

        String pathSuffix = destination.startsWith(DEFAULT_PATH) ? destination.substring(DEFAULT_PATH.length()) : "";

        if (pathSuffix.isEmpty() && !destination.equals(DEFAULT_PATH_NO_TRAILING_SLASH)) {
            log.error("Destination path suffix is empty.");
            throw new IllegalArgumentException("Destination path is too short.");
        }

        log.info("Parsed friendLoginId from path: {}", pathSuffix);
        return pathSuffix;
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

    private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (Objects.isNull(sessionAttributes)) {
            throw new WebSocketException("SessionAttributes가 Null 입니다.");
        }
        return sessionAttributes;
    }
}
