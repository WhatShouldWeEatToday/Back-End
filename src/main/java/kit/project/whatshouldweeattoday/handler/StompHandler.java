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
                log.info("Headers: {}", accessor.toNativeHeaderMap());
                String friendLoginIds = accessor.getFirstNativeHeader("friendLoginIds");
                log.info("Extracted friendLoginIds: {}", friendLoginIds);

                if (friendLoginIds == null || friendLoginIds.isEmpty()) {
                    throw new WebSocketException("friendLoginIds를 헤더에서 추출할 수 없습니다.");
                }

                List<String> friendLoginIdList = Arrays.stream(friendLoginIds.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());

                String loginId = (String) getValue(accessor, "loginId");
                log.info("User subscribed: loginId = {}, friendLoginIdList = {}", loginId, friendLoginIdList);
                setValue(accessor, "friendLoginIds", friendLoginIds);
                validateMemberInFriendship(loginId, friendLoginIdList);
//                String loginId = (String) getValue(accessor, "loginId");
//                List<String> friendLoginIds = parseFriendLoginIdFromPath(accessor);
//                log.info("User subscribed: loginId = {}, friendLoginIds = {}", loginId, friendLoginIds);
//                setValue(accessor, "friendLoginIds", String.join(",", friendLoginIds));
//                validateMemberInFriendship(loginId, friendLoginIds);

            } else if (StompCommand.DISCONNECT.equals(command)) {
                String loginId = (String) getValue(accessor, "loginId");
                log.info("WebSocket DISCONNECTED request received: loginId = {}", loginId);
            }

            log.info("header = {}", message.getHeaders());
            log.info("message = {}", message);
        } catch (Exception e) {
            log.error("Error during preSend: ", e);
            throw e; // 예외를 다시 던져서 상위 레이어에서 처리할 수 있게 합니다.
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
        jwtTokenProvider.validateToken(accessToken); // 예외 발생 가능

        return accessToken;
    }

    private List<String> parseFriendLoginIdFromPath(StompHeaderAccessor accessor) {
        String friendLoginIds = accessor.getFirstNativeHeader("friendLoginIds");
        if (friendLoginIds == null || friendLoginIds.isEmpty()) {
            throw new WebSocketException("friendLoginIds를 헤더에서 추출할 수 없습니다.");
        }
        // friendLoginIds를 쉼표(,)로 구분하여 리스트로 변환
        return Arrays.stream(friendLoginIds.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private void validateMemberInFriendship(String memberLoginId, List<String> friendLoginIds) {
        for (String friendLoginId : friendLoginIds) {
            friendshipRepository.findOneByMemberLoginIdAndFriendLoginId(memberLoginId, friendLoginId)
                    .orElseThrow(() -> new WebSocketException("조회된 friendship 결과가 없습니다."));
        }
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
