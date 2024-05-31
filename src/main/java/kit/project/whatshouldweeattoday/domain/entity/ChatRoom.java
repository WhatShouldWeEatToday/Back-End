package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;
    private String roomName;

    @ManyToMany
    @JoinTable(
            name = "chat_room_members",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private Set<Member> members = new HashSet<>();

    @Builder
    public ChatRoom(String roomName) {
        this.roomName = roomName;
    }

    public static ChatRoom createRoom(String roomName) {
        return ChatRoom.builder()
                .roomName(roomName)
                .build();
    }

    public void addMember(Member member) {
        this.members.add(member);
    }

    public void addMembers(Set<Member> members) {
        this.members.addAll(members);
    }
}