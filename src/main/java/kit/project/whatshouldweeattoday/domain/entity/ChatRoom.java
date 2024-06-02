package kit.project.whatshouldweeattoday.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    private String hostId; // 방장 닉네임
    private String roomName;
    private int currentUserNum; // 현재 인원 수

    @JsonIgnore
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private Set<ChatRoomMember> chatRoomMembers = new HashSet<>(); // 방 참여자들 (연관 관계)

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "VOTE_ID")
    private Vote vote;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "MEET_ID")
    private Meet meet;

    public void addVote(Vote vote) {
        this.vote = vote;
        vote.setRoom(this); // 연관 관계 설정
    }

    public void addMeet(Meet meet) {
        this.meet = meet;
        if (meet != null) {
            meet.setRoom(this); // 연관 관계 설정
        }
    }

//    public ChatRoom(String roomName, String createdBy, Long realRoomId) {
//        this.roomName = roomName;
//        this.createdBy = createdBy;
//        this.realRoomId = realRoomId;
//        this.createdDate = LocalDateTime.now();
//    }

    @Builder
    public ChatRoom(String hostId, String roomName, int currentUserNum) {
        this.hostId = hostId;
        this.roomName = roomName;
        this.currentUserNum = currentUserNum;
    }

    public static ChatRoom createRoom(String hostId, String roomName) {
        return ChatRoom.builder()
                .hostId(hostId)
                .roomName(roomName)
                .build();
    }

    public void addParticipant(Member member) {
        if (chatRoomMembers.stream().noneMatch(chatRoomMember -> chatRoomMember.getMember().equals(member))) {
            ChatRoomMember chatRoomMember = new ChatRoomMember(this, member);
            this.chatRoomMembers.add(chatRoomMember);
            member.getChatRoomMembers().add(chatRoomMember);
            this.currentUserNum += 1;
        }
    }

    public void removeParticipant(Member member) {
        chatRoomMembers.removeIf(chatRoomMember -> chatRoomMember.getMember().equals(member));
        member.getChatRoomMembers().removeIf(chatRoomMember -> chatRoomMember.getRoom().equals(this));
        this.currentUserNum -= 1;
    }

    public void removeParticipantAll() {
        for (ChatRoomMember chatRoomMember : this.chatRoomMembers) {
            chatRoomMember.getMember().getChatRoomMembers().remove(chatRoomMember);
        }
        this.chatRoomMembers.clear();
        this.currentUserNum = 0;
    }
}