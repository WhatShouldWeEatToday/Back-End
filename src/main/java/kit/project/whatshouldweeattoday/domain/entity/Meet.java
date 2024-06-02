package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Meet extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEET_ID")
    private Long id;
    private String meetLocate;
    private String meetMenu;
    private LocalDateTime meetTime;

    @OneToOne(mappedBy = "meet", fetch = FetchType.LAZY)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "food_id")
    private Food food;

    @Builder
    public Meet(String meetLocate, String meetMenu, LocalDateTime meetTime) {
        this.meetLocate = meetLocate;
        this.meetMenu = meetMenu;
        this.meetTime = meetTime;
    }

    public static Meet createMeet(String meetLocate, LocalDateTime meetTime) {
        return Meet.builder()
                .meetLocate(meetLocate)
                .meetTime(meetTime)
                .build();
    }

    public void updateMeet(String meetLocate, LocalDateTime meetTime) {
        this.meetLocate = meetLocate;
        this.meetTime = meetTime;
    }

    public void setMeetMenu(String meetMenu) {
        this.meetMenu = meetMenu;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
