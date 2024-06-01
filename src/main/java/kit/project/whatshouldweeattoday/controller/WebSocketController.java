package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.Greeting;
import kit.project.whatshouldweeattoday.domain.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message, StompHeaderAccessor headerAccessor) throws Exception {
        String sessionId = headerAccessor.getSessionId();
        System.out.println("Session ID: " + sessionId);
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + message.getName() + "!");
    }
}