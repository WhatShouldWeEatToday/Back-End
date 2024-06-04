package kit.project.whatshouldweeattoday.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    // 사용자 ID를 세션과 매핑하는 맵
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket connection established with session ID: {}", session.getId());

        // WebSocket 연결에서 헤더 추출
        Map<String, List<String>> headers = session.getHandshakeHeaders();

        // 헤더에서 사용자 정보 추출
        String memberId = getHeader(headers, "memberId");
        String nickname = getHeader(headers, "nickname");

        // 사용자 정보가 없는 경우 연결을 종료합니다.
        if (memberId == null || nickname == null) {
            log.error("Invalid WebSocket connection: missing user information");
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        // 이전에 로그인한 사용자 세션을 종료합니다.
        WebSocketSession existingSession = sessionMap.put(memberId, session);
        if (existingSession != null && existingSession.isOpen()) {
            existingSession.close(CloseStatus.NORMAL);
            log.info("Closed previous session for user {} with session ID: {}", memberId, existingSession.getId());
        }

        // 새로운 세션을 맵에 추가하여 추적합니다.
        sessionMap.put(memberId, session);

        // 추출한 사용자 정보를 활용하여 추가 작업을 수행할 수 있습니다.
        log.info("User {} connected with session ID: {}", memberId, session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("Received WebSocket message: {}", message.getPayload());
        // 메시지를 처리하는 로직을 추가합니다.
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket transport error: {}", exception.getMessage());
        // 오류를 처리하는 로직을 추가합니다.
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("WebSocket connection closed with session ID: {}. Close status: {}", session.getId(), closeStatus);

        // 세션이 종료될 때 맵에서 제거합니다.
        sessionMap.values().remove(session);
    }

    private String getHeader(Map<String, List<String>> headers, String key) {
        List<String> values = headers.get(key);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }
}
