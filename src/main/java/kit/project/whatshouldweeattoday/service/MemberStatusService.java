package kit.project.whatshouldweeattoday.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberStatusService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastUserStatus(String loginId, String status) {
        messagingTemplate.convertAndSend("/topic/user-status", new UserStatusMessage(loginId, status));
    }

    public static class UserStatusMessage {
        private String loginId;
        private String status;

        public UserStatusMessage(String loginId, String status) {
            this.loginId = loginId;
            this.status = status;
        }

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}