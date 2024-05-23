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
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

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
        log.info("Received a new web socket connection");
    }

    /**
     * 구독 요청
     */
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) throws BadRequestException {
        log.info("Received a new web socket subscribe");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String nickname = (String) getValue(accessor, "nickname");
        Long memberId = (Long) getValue(accessor, "memberId");
        Long friendId = (Long) getValue(accessor, "friendId");

        log.info("Member: {} {} Disconnected Crew : {}", memberId, nickname, friendId);

        ChatRoomMessage chatRoomMessage = new ChatRoomMessage(
                MessageType.JOIN, memberId, nickname + "님이 입장하셨습니다."
        );
        messagingTemplate.convertAndSend("/topic/public/" + friendId, chatRoomMessage);
    }

    /**
     * 연결 해제
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionSubscribeEvent event) throws BadRequestException {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String nickname = (String) getValue(accessor, "nickname");
        Long memberId = (Long) getValue(accessor, "memberId");
        Long friendId = (Long) getValue(accessor, "friendId");

        log.info("Member: {} {} Disconnected Crew : {}", memberId, nickname, friendId);

        ChatRoomMessage chatRoomMessage = new ChatRoomMessage(
                MessageType.LEAVE, memberId, nickname + "님이 떠났습니다."
        );
        messagingTemplate.convertAndSend("/topic/public/" + friendId, chatRoomMessage);
    }

    private Object getValue(StompHeaderAccessor accessor, String key) throws BadRequestException {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        Object value = sessionAttributes.get(key);

        if(Objects.isNull(value)) {
            throw new BadRequestException(key + " 에 해당하는 값이 없습니다.");
        }
        return value;
    }

    private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) throws BadRequestException {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if(Objects.isNull(sessionAttributes)) {
            throw new BadRequestException("SessionAttributes가 null입니다.");
        }
        return sessionAttributes;
    }
}
