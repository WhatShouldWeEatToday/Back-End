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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
        log.info("Destination: {}", accessor.getDestination());

        try {
            String destination = accessor.getDestination();

            if (StompCommand.CONNECT.equals(command)) {
                Member member = getMemberByAuthorizationHeader(accessor.getFirstNativeHeader("Authorization"));
                String sessionId = accessor.getSessionId();
                setSessionAttributes(accessor, member, sessionId);

            } else if (StompCommand.SUBSCRIBE.equals(command)) {
                if (destination == null) {
                    log.error("Destination is null.");
                    throw new IllegalArgumentException("Destination cannot be null.");
                }
                if (destination.startsWith("/topic/room/")) {
                    String roomIdStr = extractPathSuffix(destination, "/topic/room/");
                    if (!roomIdStr.isEmpty()) {
                        Long roomId = Long.valueOf(roomIdStr);
                        setLongValue(accessor, "roomId", roomId);
                    } else {
                        log.error("Empty roomId extracted from destination: {}", destination);
                    }
                }
            } else if (StompCommand.SEND.equals(command)) {
                if (destination.startsWith("/app/vote/register/")) {
                    Long roomId = Long.valueOf(extractPathSuffix(destination, "/app/vote/register/"));
                    setLongValue(accessor, "roomId", roomId);
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader == null || authHeader.isBlank()) {
                        throw new WebSocketException("Authorization header is missing");
                    }
                    Member member = getMemberByAuthorizationHeader(authHeader);
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(member, null, null)
                    );

                } else if (destination.startsWith("/app/vote/increment/")) {
                    String[] pathSegments = destination.substring("/app/vote/increment/".length()).split("/");
                    if (pathSegments.length == 2) {
                        Long roomId = Long.valueOf(pathSegments[0]);
                        Long voteId = Long.valueOf(pathSegments[1]);
                        setLongValue(accessor, "roomId", roomId);
                        setLongValue(accessor, "voteId", voteId);
                    } else {
                        log.error("Invalid destination format for increment: {}", destination);
                        throw new IllegalArgumentException("Invalid destination format");
                    }

                } else if (destination.startsWith("/app/vote/end/")) {
                    String[] pathSegments = destination.substring("/app/vote/end/".length()).split("/");
                    if (pathSegments.length == 2) {
                        Long roomId = Long.valueOf(pathSegments[0]);
                        Long voteId = Long.valueOf(pathSegments[1]);
                        setLongValue(accessor, "roomId", roomId);
                        setLongValue(accessor, "voteId", voteId);
                    } else {
                        log.error("Invalid destination format for end: {}", destination);
                        throw new IllegalArgumentException("Invalid destination format");
                    }

                } else if (destination.startsWith("/app/meet/register/")) {
                    String[] pathSegments = destination.substring("/app/meet/register/".length()).split("/");
                    if (pathSegments.length == 2) {
                        Long roomId = Long.valueOf(pathSegments[0]);
                        Long meetId = Long.valueOf(pathSegments[1]);
                        setLongValue(accessor, "roomId", roomId);
                        setLongValue(accessor, "meetId", meetId);
                    } else {
                        log.error("Invalid destination format for meet register: {}", destination);
                        throw new IllegalArgumentException("Invalid destination format");
                    }

                } else if (destination.startsWith("/departure/register/")) {
                    Long roomId = Long.valueOf(extractPathSuffix(destination, "/departure/register/"));
                    setLongValue(accessor, "roomId", roomId);
                } else {
                    log.error("Unsupported destination: {}", destination);
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
        int prefixLength = prefix.length();
        return destination.substring(prefixLength);
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
