package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import kit.project.whatshouldweeattoday.domain.type.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Chat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHAT_ID")
    private Long id;
    private String name;
    private String message;
    private MessageType messageType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "meet_id")
    private Meet meet;

    @OneToMany(mappedBy = "chat", orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "notice_id")
    private Notice notice;
}
