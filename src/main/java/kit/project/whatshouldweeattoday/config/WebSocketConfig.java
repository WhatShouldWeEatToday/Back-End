package kit.project.whatshouldweeattoday.config;

import kit.project.whatshouldweeattoday.handler.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app"); // 발행자가 "/"의 경로로 메시지를 주면 가공을 해서 구독자들에게 전달
        registry.enableSimpleBroker("/topic"); // 발행자가 "/"의 경로로 메시지를 주면 구독자들에게 전달
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 주소 : ws://localhost:8080/ws-stomp
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*") // CORS 허용 범위
                .withSockJS(); // 버전 낮은 브라우저에서도 적용 가능
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
        registration.taskExecutor()
                .corePoolSize(20)
                .maxPoolSize(50)
                .queueCapacity(400)
                .keepAliveSeconds(60);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
                .corePoolSize(20)
                .maxPoolSize(50)
                .queueCapacity(400)
                .keepAliveSeconds(60);
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}