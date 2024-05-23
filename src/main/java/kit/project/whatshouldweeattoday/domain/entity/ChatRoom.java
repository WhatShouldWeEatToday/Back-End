package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;
    private String name;

    @ManyToMany
    @JoinTable(
            name = "chat_room_members",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private Set<Member> members = new HashSet<>();

    @Builder
    public ChatRoom(String name) {
        this.name = name;
    }

    public static ChatRoom createRoom(String name) {
        return ChatRoom.builder()
                .name(name)
                .build();
    }

    public void addMember(Member member) {
        this.members.add(member);
    }

    public void addMembers(Set<Member> members) {
        this.members.addAll(members);
    }
}