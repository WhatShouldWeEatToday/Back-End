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
import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = accessor.getUser();
        String sessionId = accessor.getSessionId(); // 세션 ID 추출
        log.info("Received a new web socket connection with session ID: {}", sessionId);
        if (user != null) {
            log.info("Received a new web socket connection from user: {}", user.getName());
        } else {
            // 인증되지 않은 사용자의 연결을 거부하고 오류 메시지를 전송하여 연결 종료
            messagingTemplate.convertAndSendToUser(accessor.getSessionId(), "/queue/errors", "Unauthorized connection");
            // 세션 속성이 null인 경우 초기화
            if (accessor.getSessionAttributes() == null) {
                accessor.setSessionAttributes(new HashMap<>());
            }

            // 세션 속성에 disconnectAfterSend 속성 추가
            accessor.getSessionAttributes().put("disconnectAfterSend", true);
        }
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = accessor.getUser();
        if (user != null) {
            log.info("User {} subscribed", user.getName());
        } else {
            // 인증되지 않은 사용자의 연결을 거부하고 오류 메시지를 전송하여 연결 종료
            messagingTemplate.convertAndSendToUser(accessor.getSessionId(), "/queue/errors", "Unauthorized connection");
            // 세션 속성이 null인 경우 초기화
            if (accessor.getSessionAttributes() == null) {
                accessor.setSessionAttributes(new HashMap<>());
            }

            // 세션 속성에 disconnectAfterSend 속성 추가
            accessor.getSessionAttributes().put("disconnectAfterSend", true);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = accessor.getUser();
        String sessionId = accessor.getSessionId(); // 세션 ID 추출
        log.info("Received a new web socket connection with session ID: {}", sessionId);
        if (user != null) {
            log.info("User Disconnected: {}", user.getName());
        }
    }


}
