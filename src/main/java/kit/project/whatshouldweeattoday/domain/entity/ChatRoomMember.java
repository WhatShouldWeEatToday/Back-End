package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom room;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public ChatRoomMember(ChatRoom room, Member member) {
        this.room = room;
        this.member = member;
    }
}