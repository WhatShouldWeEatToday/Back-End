package kit.project.whatshouldweeattoday.handler;

import kit.project.whatshouldweeattoday.domain.dto.chat.ChatRoomMessage;
import kit.project.whatshouldweeattoday.domain.type.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * 연결 요청
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = accessor.getUser();
        if (user != null) {
            log.info("Received a new web socket connection from user: {}", user.getName());
        }
    }

    /**
     * 구독 요청
     */
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) throws BadRequestException {
        log.info("Received a new web socket subscribe");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String nickname = getValue(accessor, "nickname");
        String loginId = getValue(accessor, "loginId");
        String friendLoginId = getValue(accessor, "friendLoginId");

        log.info("Member: {} {} Disconnected Crew : {}", loginId, nickname, friendLoginId);

        ChatRoomMessage chatRoomMessage = new ChatRoomMessage(
                MessageType.JOIN, loginId, nickname + "님이 입장하셨습니다."
        );
        messagingTemplate.convertAndSend("/topic/public/" + friendLoginId, chatRoomMessage);
    }

    /**
     * 연결 해제
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) throws BadRequestException {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String nickname = getValue(accessor, "nickname");
        String loginId = getValue(accessor, "loginId");
        String friendLoginId = getValue(accessor, "friendLoginId");

        log.info("Member: {} {} Disconnected Crew : {}", loginId, nickname, friendLoginId);

        ChatRoomMessage chatRoomMessage = new ChatRoomMessage(
                MessageType.LEAVE, loginId, nickname + "님이 떠났습니다."
        );

        if (friendLoginId != null) {
            messagingTemplate.convertAndSend("/topic/public/" + friendLoginId, chatRoomMessage); // {9}
        } else {
            log.error("friendLoginId is null.");
        }
    }

    private String getValue(StompHeaderAccessor accessor, String key) throws BadRequestException {
        try {
            Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
            Object value = sessionAttributes.get(key);

            if (Objects.isNull(value)) {
                throw new BadRequestException(key + " 에 해당하는 값이 없습니다.");
            }
            return String.valueOf(value);
        } catch (Exception e) {
            throw new BadRequestException("값을 가져오는 동안 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) throws BadRequestException {
        try {
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

            if (Objects.isNull(sessionAttributes)) {
                throw new BadRequestException("SessionAttributes가 null입니다.");
            }
            return sessionAttributes;
        } catch (Exception e) {
            throw new BadRequestException("세션 속성을 가져오는 동안 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
