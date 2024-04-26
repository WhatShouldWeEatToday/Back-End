package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import kit.project.whatshouldweeattoday.domain.type.FriendshipStatus;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userLoginId;
    private String friendLoginId;
    private FriendshipStatus status;
    private boolean isFrom; // 상대에게 온 요청
    private Long counterpartId; // 상대가 보낸 요청 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void acceptFriendshipRequest() {
        status = FriendshipStatus.ACCEPT;
    }

    public void setCounterpartId(Long id) {
        counterpartId = id;
    }
}
