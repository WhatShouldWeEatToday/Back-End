package kit.project.whatshouldweeattoday.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/room"); // 응답 prefix
        registry.setApplicationDestinationPrefixes("/send"); // 송신 prefix
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 주소 : ws://localhost:8080/ws-stomp
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*") // CORS 허용 범위
                .withSockJS(); // 버전 낮은 브라우저에서도 적용 가능
    }
}
