package kit.project.whatshouldweeattoday.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private String hostId; // 방장 닉네임
    private String roomName;
    private int currentUserNum; // 현재 인원 수

    @JsonIgnore
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Member> participants = new HashSet<>(); // 방 참여자들 (연관 관계)


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
        this.participants.add(member);
        member.setMappingRoom(this);
        this.currentUserNum += 1;
    }

    public void removeParticipant(Member member) {
        this.participants.remove(member);
        this.currentUserNum -= 1;
    }

    // 방 삭제하기 위해, 모두 내보내기
    public void removeParticipantAll() {
        // user - room 매핑 관계 끊기
        for (Member participant : this.participants) {
            participant.unsetMappingRoom();
        }
        // 회원정보 List<> 비우기
        this.participants.clear();
        this.currentUserNum = 0;
    }

    public Set<Member> getParticipants() {
        return participants;
    }
}