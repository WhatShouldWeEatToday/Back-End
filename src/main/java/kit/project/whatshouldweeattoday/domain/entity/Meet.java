package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Meet extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEET_ID")
    private Long id;
    private String meetLocate;
    private String meetMenu;
    private Date meetTime;

    @OneToOne(mappedBy = "meet", fetch = FetchType.LAZY)
    private Chat chat;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "food_id")
    private Food food;

    @Builder
    public Meet(String meetLocate, String meetMenu, Date meetTime) {
        this.meetLocate = meetLocate;
        this.meetMenu = meetMenu;
        this.meetTime = meetTime;
    }

    public static Meet createMeet(String meetLocate, String meetMenu, Date meetTime) {
        return Meet.builder()
                .meetLocate(meetLocate)
                .meetMenu(meetMenu)
                .meetTime(meetTime)
                .build();
    }

    public void updateMeet(String meetLocate, String meetMenu, Date meetTime) {
        this.meetLocate = meetLocate;
        this.meetMenu = meetMenu;
        this.meetTime = meetTime;
    }
}
